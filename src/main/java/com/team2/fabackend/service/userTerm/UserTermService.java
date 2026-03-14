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
     * Retrieves the list of currently active terms.
     *
     * @return A list of TermInfoResponse objects representing active terms.
     */
    public List<TermInfoResponse> getActiveTerms() {
        return userTermReader.findActiveTerms().stream()
                .map(TermInfoResponse::from)
                .toList();
    }

    /**
     * Processes term agreement for a user.
     *
     * @param userId        The ID of the user agreeing to terms.
     * @param agreedTermIds The list of IDs of the terms being agreed to.
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
     * Retrieves the term agreement status for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of UserTermStatusResponse objects for the user.
     */
    public List<UserTermStatusResponse> getUserTermStatus(Long userId) {
        User user = userReader.findById(userId);
        return userTermReader.findUserTermStatus(user);
    }

    /**
     * Creates a new term record.
     *
     * @param request The term details to be saved.
     * @return A TermInfoResponse representing the newly created term.
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
     * Updates an existing term record.
     *
     * @param termId  The ID of the term to be updated.
     * @param request The new term details.
     * @return A TermInfoResponse representing the updated term.
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
