package com.team2.fabackend.api.goals.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "카테고리별 통계 데이터")
public class CategoryStatResponse {
    @Schema(description = "카테고리 이름", example = "식비")
    private String category;
    @Schema(description = "해당 카테고리의 지출/저축 합계 금액", example = "450000")
    private Long amount;
}
