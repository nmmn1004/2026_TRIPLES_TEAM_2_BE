package com.team2.fabackend.api.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiBudgetGoalDto {
    private Long foodAmount;
    private Long transportAmount;
    private Long leisureAmount;
    private Long fixedAmount;
    private Long totalAmount;
}
