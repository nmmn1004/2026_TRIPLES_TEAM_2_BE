package com.team2.fabackend.domain.budget;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<BudgetGoal, Long> {
    Optional<BudgetGoal> findByUserId(Long userId);
}
