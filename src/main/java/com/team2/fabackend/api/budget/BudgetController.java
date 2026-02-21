package com.team2.fabackend.api.budget;

import com.team2.fabackend.api.budget.dto.BudgetRequest;
import com.team2.fabackend.api.budget.dto.BudgetResponse;
import com.team2.fabackend.api.budget.dto.BudgetUpdateRequest;
import com.team2.fabackend.domain.budget.BudgetGoal;
import com.team2.fabackend.service.budget.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @PostMapping("/{userId}")
    public Long saveBudget(@RequestBody BudgetRequest request, @PathVariable Long userId) {
        return budgetService.saveBudget(request, userId);
    }

    @GetMapping("/{userId}")
    public BudgetResponse getBudget(@PathVariable Long userId) {
        return budgetService.getBudget(userId);
    }

    @PatchMapping("/{userId}/amounts")
    public Long updateAmounts(@PathVariable Long userId, @RequestBody BudgetUpdateRequest request) {
        return budgetService.updateBudgetAmounts(userId, request);
    }
}
