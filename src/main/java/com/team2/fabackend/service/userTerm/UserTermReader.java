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
     * Finds a term by its ID.
     *
     * @param termId The ID of the term.
     * @return The found term entity.
     * @throws CustomException If the term is not found.
     */
    public Term findById(Long termId) {
        return termRepository.findById(termId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_DATA_VALUE));
    }

    /**
     * Retrieves all currently active terms.
     *
     * @return A list of all active terms.
     */
    public List<Term> findActiveTerms() {
        return termRepository.findAll();
    }

    /**
     * Retrieves the IDs of all terms that a user has already agreed to.
     *
     * @param user The user entity.
     * @return A set of agreed term IDs.
     */
    public Set<Long> findAgreedTermIds(User user) {
        return userTermRepository.findByUserAndAgreedTrue(user).stream()
                .map(userTerm -> userTerm.getTerm().getId())
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves all UserTerm records for a specific user.
     *
     * @param user The user entity.
     * @return A list of UserTerm entities.
     */
    public List<UserTerm> findUserTerms(User user) {
        return userTermRepository.findByUser(user);
    }

    /**
     * Validates that the user is agreeing to all required terms and that the IDs provided are valid.
     *
     * @param activeTerms    The list of currently active terms.
     * @param agreedTermIds  The IDs of the terms being agreed to.
     * @throws IllegalStateException    If a required term is missing.
     * @throws IllegalArgumentException If an invalid term ID is provided.
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
     * Retrieves the term agreement status for a user based on all currently active terms.
     *
     * @param user The user entity.
     * @return A list of UserTermStatusResponse objects for the user.
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
