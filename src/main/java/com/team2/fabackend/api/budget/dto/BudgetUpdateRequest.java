package com.team2.fabackend.api.budget.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BudgetUpdateRequest {
    private Long foodAmount;
    private Long transportAmount;
    private Long leisureAmount;
    private Long fixedAmount;
}
