package com.team2.fabackend.api.goals.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "저축 목표 생성/수정 요청 정보")
public class GoalRequest {
    @Schema(description = "목표 제목 (예: 유럽 여행, 자동차 구매)", example = "아이맥 사기", nullable = false)
    private String title;
    @Schema(description = "목표 카테고리", example = "전자제품")
    private String category;
    @Schema(description = "최종 목표 금액", example = "3000000", nullable = false)
    private Long targetAmount;
    @Schema(description = "목표 시작일 (yyyy-MM-dd)", example = "2026-01-01")
    private LocalDate startDate;
    @Schema(description = "목표 종료일 (yyyy-MM-dd)", example = "2026-12-31")
    private LocalDate endDate;
    @Schema(description = "목표 관련 메모", example = "M4 칩 탑재된 모델로!")
    private String memo;
}
