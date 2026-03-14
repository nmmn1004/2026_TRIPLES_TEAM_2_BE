package com.team2.fabackend.domain.budget;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<BudgetGoal, Long> {
    /**
     * Finds a budget goal associated with a specific user ID.
     *
     * @param userId The ID of the user.
     * @return An Optional containing the BudgetGoal if found.
     */
    Optional<BudgetGoal> findByUserId(Long userId);
}
