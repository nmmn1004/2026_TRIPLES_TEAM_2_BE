package com.team2.fabackend.service.userTerm;

import com.team2.fabackend.api.term.dto.TermInfoResponse;
import com.team2.fabackend.api.term.dto.TermSaveRequest;
import com.team2.fabackend.api.term.dto.UserTermStatusResponse;
import com.team2.fabackend.domain.term.Term;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.domain.userTerm.UserTerm;
import com.team2.fabackend.service.user.UserReader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserTermService {
    private final UserReader userReader;
    private final UserTermReader userTermReader;
    private final UserTermWriter userTermWriter;

    /**
     * 현재 활성화된 약관 리스트를 조회합니다.
     *
     * @return 활성화된 약관을 나타내는 TermInfoResponse 객체 리스트.
     */
    public List<TermInfoResponse> getActiveTerms() {
        return userTermReader.findActiveTerms().stream()
                .map(TermInfoResponse::from)
                .toList();
    }

    /**
     * 사용자의 약관 동의를 처리합니다.
     *
     * @param userId        약관에 동의하는 사용자의 ID.
     * @param agreedTermIds 동의한 약관 ID 리스트.
     */
    public void agreeTerms(Long userId, List<Long> agreedTermIds) {
        User user = userReader.findById(userId);

        List<Term> activeTerms = userTermReader.findActiveTerms();

        userTermReader.validateAgreement(activeTerms, agreedTermIds);

        Set<Long> alreadyAgreed = userTermReader.findAgreedTermIds(user);

        List<UserTerm> newUserTerms =
                UserTerm.agreeNewTerms(user, activeTerms, agreedTermIds, alreadyAgreed);

        userTermWriter.saveAll(newUserTerms);
    }

    /**
     * 특정 사용자의 약관 동의 상태를 조회합니다.
     *
     * @param userId 사용자의 ID.
     * @return 사용자의 UserTermStatusResponse 객체 리스트.
     */
    public List<UserTermStatusResponse> getUserTermStatus(Long userId) {
        User user = userReader.findById(userId);
        return userTermReader.findUserTermStatus(user);
    }

    /**
     * 새로운 약관 레코드를 생성합니다.
     *
     * @param request 저장할 약관 상세 정보.
     * @return 새로 생성된 약관을 나타내는 TermInfoResponse.
     */
    public TermInfoResponse createTerm(TermSaveRequest request) {
        Term term = Term.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .version(request.getVersion())
                .required(request.isRequired())
                .build();

        Term saved = userTermWriter.createTerm(term);
        return TermInfoResponse.from(saved);
    }

    /**
     * 기존 약관 레코드를 업데이트합니다.
     *
     * @param termId  업데이트할 약관의 ID.
     * @param request 새로운 약관 상세 정보.
     * @return 업데이트된 약관을 나타내는 TermInfoResponse.
     */
    public TermInfoResponse updateTerm(Long termId, TermSaveRequest request) {
        Term term = userTermReader.findById(termId);

        term.updateTerm(
                request.getTitle() == null ? term.getTitle() : request.getTitle(),
                request.getContent() == null ? term.getContent() : request.getContent(),
                request.getVersion() == null ? term.getVersion() : request.getVersion(),
                request.isRequired()
        );

        return TermInfoResponse.from(term);
    }
}
