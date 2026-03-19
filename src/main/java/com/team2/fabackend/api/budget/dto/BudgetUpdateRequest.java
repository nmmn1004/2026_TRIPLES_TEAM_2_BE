package com.team2.fabackend.api.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "예산 금액 직접 수정 요청")
public class BudgetUpdateRequest {
    @Schema(description = "식비 예산 직접 입력", example = "500000")
    private Long foodAmount;
    @Schema(description = "교통비 예산 직접 입력", example = "120000")
    private Long transportAmount;
    @Schema(description = "여가비 예산 직접 입력", example = "200000")
    private Long leisureAmount;
    @Schema(description = "고정 지출 예산 직접 입력", example = "450000")
    private Long fixedAmount;
}
