package com.team2.fabackend.api.budget.dto;

import com.team2.fabackend.domain.budget.BudgetGoal;
import lombok.Getter;

@Getter
public class BudgetResponse {
    private Long id;
    private Long totalAmount;

    private Long foodAmount;
    private Long transportAmount;
    private Long leisureAmount;
    private Long fixedAmount;

    private double foodPercent;
    private double transportPercent;
    private double leisurePercent;
    private double fixedPercent;

    public BudgetResponse(BudgetGoal goal) {
        this.id = goal.getId();
        this.totalAmount = goal.getTotalAmount();
        this.foodAmount = goal.getFoodAmount();
        this.transportAmount = goal.getTransportAmount();
        this.leisureAmount = goal.getLeisureAmount();
        this.fixedAmount = goal.getFixedAmount();

        if(this.totalAmount > 0) {
            this.foodPercent = calculatePercent(goal.getFoodAmount());
            this.transportPercent = calculatePercent(goal.getTransportAmount());
            this.leisurePercent = calculatePercent(goal.getLeisureAmount());
            this.fixedPercent = calculatePercent(goal.getFixedAmount());
        }
    }

    private double calculatePercent(Long amount) {
        return Math.round(((double) amount/totalAmount*100)*10) / 10.0;
    }
}
