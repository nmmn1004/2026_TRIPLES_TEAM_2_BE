package com.team2.fabackend.domain.userTerm;

import com.team2.fabackend.domain.term.Term;
import com.team2.fabackend.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_terms")
public class UserTerm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private Term term;

//  동의 여부
    private boolean agreed;
//  동의 날짜
    private LocalDateTime agreedAt;

    @Builder
    private UserTerm(User user, Term term, boolean agreed, LocalDateTime agreedAt) {
        this.user = user;
        this.term = term;
        this.agreed = agreed;
        this.agreedAt = agreedAt;
    }

    /** 유저가 약관에 동의 */
    public static UserTerm agree(User user, Term term) {
        return UserTerm.builder()
                .user(user)
                .term(term)
                .agreed(true)
                .agreedAt(LocalDateTime.now())
                .build();
    }

    public static List<UserTerm> agreeNewTerms(
            User user,
            List<Term> activeTerms,
            Collection<Long> agreedTermIds,
            Set<Long> alreadyAgreed
    ) {
        Map<Long, Term> termMap = activeTerms.stream()
                .collect(Collectors.toMap(Term::getId, t -> t));

        return agreedTermIds.stream()
                .filter(termId -> !alreadyAgreed.contains(termId))
                .map(termId -> UserTerm.agree(user, termMap.get(termId)))
                .toList();
    }

}
