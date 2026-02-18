package com.team2.fabackend.service.budget;

import com.team2.fabackend.domain.budget.BudgetGoal;
import com.team2.fabackend.domain.budget.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetReader {
    private final BudgetRepository budgetRepository;

    public BudgetGoal getById(Long userId) {
        BudgetGoal goal = budgetRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("설정된 예산 목표가 없습니다. 유저 ID: " + userId));

        return goal;
    }
}
