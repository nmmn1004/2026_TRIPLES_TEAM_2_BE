package com.team2.fabackend.api.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "카테고리별 예산 정보 응답")
public class BudgetResponse {
    @Schema(description = "예산 ID", example = "1")
    private Long id;
    @Schema(description = "한 달 전체 예산 금액 합계", example = "1200000")
    private Long totalAmount;

    @Schema(description = "식비 예산 금액", example = "400000")
    private Long foodAmount;
    @Schema(description = "교통비 예산 금액", example = "100000")
    private Long transportAmount;
    @Schema(description = "여가비 예산 금액", example = "300000")
    private Long leisureAmount;
    @Schema(description = "고정 지출 예산 금액", example = "400000")
    private Long fixedAmount;

    @Schema(description = "전체 예산 중 식비 비중 (%)", example = "33.3")
    private double foodPercent;
    @Schema(description = "전체 예산 중 교통비 비중 (%)", example = "8.3")
    private double transportPercent;
    @Schema(description = "전체 예산 중 여가비 비중 (%)", example = "25.0")
    private double leisurePercent;
    @Schema(description = "전체 예산 중 고정 지출 비중 (%)", example = "33.3")
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
