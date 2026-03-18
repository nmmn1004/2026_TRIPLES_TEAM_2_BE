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

    /**
     * 사용자 약관 동의 리스트를 저장합니다.
     *
     * @param userTerms 저장할 UserTerm 엔티티 리스트.
     */
    public void saveAll(List<UserTerm> userTerms) {
        userTermRepository.saveAll(userTerms);
    }

    /**
     * 새로운 약관 엔티티를 저장합니다.
     *
     * @param term 생성할 약관 엔티티.
     * @return 저장된 약관 엔티티.
     */
    public Term createTerm(Term term) {
        return termRepository.save(term);
    }
}
