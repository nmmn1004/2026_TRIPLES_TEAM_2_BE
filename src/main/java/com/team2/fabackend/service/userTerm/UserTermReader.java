package com.team2.fabackend.service.userTerm;

import com.team2.fabackend.api.term.dto.UserTermStatusResponse;
import com.team2.fabackend.domain.term.Term;
import com.team2.fabackend.domain.term.TermRepository;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.domain.userTerm.UserTerm;
import com.team2.fabackend.domain.userTerm.UserTermRepository;
import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserTermReader {
    private final TermRepository termRepository;
    private final UserTermRepository userTermRepository;

    /**
     * ID로 약관을 찾습니다.
     *
     * @param termId 약관의 ID.
     * @return 찾은 약관 엔티티.
     * @throws CustomException 약관을 찾을 수 없는 경우.
     */
    public Term findById(Long termId) {
        return termRepository.findById(termId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_DATA_VALUE));
    }

    /**
     * 현재 활성화된 모든 약관을 조회합니다.
     *
     * @return 모든 활성화된 약관 리스트.
     */
    public List<Term> findActiveTerms() {
        return termRepository.findAll();
    }

    /**
     * 사용자가 이미 동의한 모든 약관의 ID를 조회합니다.
     *
     * @param user 사용자 엔티티.
     * @return 동의한 약관 ID 셋.
     */
    public Set<Long> findAgreedTermIds(User user) {
        return userTermRepository.findByUserAndAgreedTrue(user).stream()
                .map(userTerm -> userTerm.getTerm().getId())
                .collect(Collectors.toSet());
    }

    /**
     * 특정 사용자의 모든 UserTerm 레코드를 조회합니다.
     *
     * @param user 사용자 엔티티.
     * @return UserTerm 엔티티 리스트.
     */
    public List<UserTerm> findUserTerms(User user) {
        return userTermRepository.findByUser(user);
    }

    /**
     * 사용자가 모든 필수 약관에 동의했는지, 제공된 ID가 유효한지 검증합니다.
     *
     * @param activeTerms    현재 활성화된 약관 리스트.
     * @param agreedTermIds  동의할 약관 ID 리스트.
     * @throws IllegalStateException    필수 약관이 누락된 경우.
     * @throws IllegalArgumentException 유효하지 않은 약관 ID가 제공된 경우.
     */
    public void validateAgreement(List<Term> activeTerms, List<Long> agreedTermIds) {

        Map<Long, Term> termMap = activeTerms.stream()
                .collect(Collectors.toMap(Term::getId, t -> t));

        List<Long> requiredTermIds = activeTerms.stream()
                .filter(Term::isRequired)
                .map(Term::getId)
                .toList();

        if (!agreedTermIds.containsAll(requiredTermIds)) {
            throw new IllegalStateException("필수 약관 미동의");
        }

        for (Long termId : agreedTermIds) {
            if (!termMap.containsKey(termId)) {
                throw new IllegalArgumentException("유효하지 않은 약관 ID: " + termId);
            }
        }
    }

    /**
     * 모든 현재 활성화된 약관을 기반으로 사용자의 약관 동의 상태를 조회합니다.
     *
     * @param user 사용자 엔티티.
     * @return 사용자의 UserTermStatusResponse 객체 리스트.
     */
    public List<UserTermStatusResponse> findUserTermStatus(User user) {
        List<Term> activeTerms = termRepository.findAll();
        List<UserTerm> userTerms = userTermRepository.findByUser(user);

        Map<Long, UserTerm> userTermMap =
                userTerms.stream()
                        .collect(Collectors.toMap(
                                ut -> ut.getTerm().getId(),
                                ut -> ut
                        ));

        return activeTerms.stream()
                .map(term -> UserTermStatusResponse.from(
                        term,
                        userTermMap.get(term.getId())
                ))
                .toList();
    }
}
