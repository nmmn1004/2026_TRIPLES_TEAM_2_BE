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

    private boolean agreed;
    private LocalDateTime agreedAt;

    /**
     * Builder 사용을 위한 프라이빗 생성자입니다.
     *
     * @param user     약관과 연관된 사용자입니다.
     * @param term     동의 대상 약관입니다.
     * @param agreed   동의 상태입니다.
     * @param agreedAt 동의 시간입니다.
     */
    @Builder
    private UserTerm(User user, Term term, boolean agreed, LocalDateTime agreedAt) {
        this.user = user;
        this.term = term;
        this.agreed = agreed;
        this.agreedAt = agreedAt;
    }

    /**
     * 동의를 나타내는 새로운 UserTerm 레코드를 생성합니다.
     *
     * @param user 약관에 동의하는 사용자입니다.
     * @param term 동의 대상 약관입니다.
     * @return 새로운 UserTerm 인스턴스입니다.
     */
    public static UserTerm agree(User user, Term term) {
        return UserTerm.builder()
                .user(user)
                .term(term)
                .agreed(true)
                .agreedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 약관 ID 목록을 처리하고 사용자가 아직 동의하지 않은 약관에 대해 새로운 UserTerm 레코드를 생성합니다.
     *
     * @param user           사용자 엔티티입니다.
     * @param activeTerms    현재 활성화된 약관 목록입니다.
     * @param agreedTermIds  사용자가 동의하는 약관 ID 모음입니다.
     * @param alreadyAgreed  사용자가 이미 동의한 약관 ID 세트입니다.
     * @return 영속화될 새로운 UserTerm 객체 목록입니다.
     */
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
