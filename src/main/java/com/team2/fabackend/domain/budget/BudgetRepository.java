package com.team2.fabackend.domain.budget;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<BudgetGoal, Long> {
    /**
     * 특정 사용자 ID와 연관된 예산 목표를 찾습니다.
     *
     * @param userId 사용자의 ID입니다.
     * @return 발견된 경우 BudgetGoal을 포함하는 Optional입니다.
     */
    Optional<BudgetGoal> findByUserId(Long userId);
}
