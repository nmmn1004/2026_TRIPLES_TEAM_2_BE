package com.team2.fabackend.service.userTerm;

import com.team2.fabackend.domain.term.Term;
import com.team2.fabackend.domain.term.TermRepository;
import com.team2.fabackend.domain.userTerm.UserTerm;
import com.team2.fabackend.domain.userTerm.UserTermRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class UserTermWriter {
    private final UserTermRepository userTermRepository;
    private final TermRepository termRepository;

    public void saveAll(List<UserTerm> userTerms) {
        userTermRepository.saveAll(userTerms);
    }

    public Term createTerm(Term term) {
        return termRepository.save(term);
    }
}
