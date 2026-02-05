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
    List<UserTerm> findByUserAndAgreedTrue(User user);
    List<UserTerm> findByUser(User user);
    boolean existsByUserAndTerm(User user, Term term);
}
