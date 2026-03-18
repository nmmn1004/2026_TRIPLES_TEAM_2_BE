package com.team2.fabackend.domain.userTerm;

import com.team2.fabackend.domain.term.Term;
import com.team2.fabackend.domain.user.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserTermRepository extends JpaRepository<UserTerm, Long> {
    /**
     * 특정 사용자가 동의한 모든 약관을 찾습니다.
     *
     * @param user 사용자 엔티티입니다.
     * @return agreed가 true인 UserTerm 항목 목록입니다.
     */
    List<UserTerm> findByUserAndAgreedTrue(User user);

    /**
     * 특정 사용자에 대한 모든 약관 연관성을 찾습니다.
     *
     * @param user 사용자 엔티티입니다.
     * @return 사용자에 대한 모든 UserTerm 항목 목록입니다.
     */
    List<UserTerm> findByUser(User user);

    /**
     * 특정 약관 연관성이 사용자에게 존재하는지 확인합니다.
     *
     * @param user 사용자 엔티티입니다.
     * @param term 약관 엔티티입니다.
     * @return 연관성이 존재하면 true, 그렇지 않으면 false입니다.
     */
    boolean existsByUserAndTerm(User user, Term term);
}
