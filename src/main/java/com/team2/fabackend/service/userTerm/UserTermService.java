package com.team2.fabackend.service.userTerm;

import com.team2.fabackend.api.term.dto.TermInfoResponse;
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

    public List<TermInfoResponse> getActiveTerms() {
        return userTermReader.findActiveTerms().stream()
                .map(TermInfoResponse::from)
                .toList();
    }

    public void agreeTerms(Long userId, List<Long> agreedTermIds) {
        User user = userReader.findById(userId);

        // 1. 현재 유효한 약관 조회
        List<Term> activeTerms = userTermReader.findActiveTerms();

        // 2. 약관 동의 정책 검증
        userTermReader.validateAgreement(activeTerms, agreedTermIds);

        // 3. 이미 동의한 약관 ID 조회
        Set<Long> alreadyAgreed = userTermReader.findAgreedTermIds(user);

        // 4. 신규 동의 약관 생성
        List<UserTerm> newUserTerms =
                UserTerm.agreeNewTerms(user, activeTerms, agreedTermIds, alreadyAgreed);

        // 5. 저장
        userTermWriter.saveAll(newUserTerms);
    }

    public List<UserTermStatusResponse> getUserTermStatus(Long userId) {
        User user = userReader.findById(userId);
        return userTermReader.findUserTermStatus(user);
    }
}
