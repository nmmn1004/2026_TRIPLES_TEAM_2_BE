package com.team2.fabackend.domain.advice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AdviceHistoryRepository extends JpaRepository<AdviceHistory, Long> {
    /**
     * Checks if an advice history record exists for a user on a specific date.
     *
     * @param userId    The ID of the user.
     * @param createdAt The date to check.
     * @return True if a record exists, false otherwise.
     */
    boolean existsByUserIdAndCreatedAt(Long userId, LocalDate createdAt);

    /**
     * Finds an advice history record for a user on a specific date.
     *
     * @param userId    The ID of the user.
     * @param createdAt The date to search for.
     * @return An Optional containing the AdviceHistory if found.
     */
    Optional<AdviceHistory> findByUserIdAndCreatedAt(Long userId, LocalDate createdAt);
}
