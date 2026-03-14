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
     * Finds all terms agreed to by a specific user.
     *
     * @param user The user entity.
     * @return A list of UserTerm entries where agreed is true.
     */
    List<UserTerm> findByUserAndAgreedTrue(User user);

    /**
     * Finds all term associations for a specific user.
     *
     * @param user The user entity.
     * @return A list of all UserTerm entries for the user.
     */
    List<UserTerm> findByUser(User user);

    /**
     * Checks if a specific term association exists for a user.
     *
     * @param user The user entity.
     * @param term The term entity.
     * @return True if the association exists, false otherwise.
     */
    boolean existsByUserAndTerm(User user, Term term);
}
