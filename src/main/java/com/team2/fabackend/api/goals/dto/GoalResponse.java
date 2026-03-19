package com.team2.fabackend.api.goals.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "저축 목표 상세 응답 정보")
public class GoalResponse {
    @Schema(description = "목표 ID", example = "1")
    private Long id;
    @Schema(description = "목표 제목", example = "아이맥 사기")
    private String title;
    @Schema(description = "목표 카테고리", example = "전자제품")
    private String category;
    @Schema(description = "최종 목표 금액", example = "3000000")
    private Long targetAmount;
    @Schema(description = "현재까지 모은 금액 (가계부 연동)", example = "1500000")
    private Long currentSpend;
    @Schema(description = "목표 상태 (진행중, 달성 등)", example = "IN_PROGRESS")
    private String status;
    @Schema(description = "목표 달성률 (%)", example = "50")
    public int progressRate;
    @Schema(description = "카테고리별 통계 데이터")
    private List<CategoryStatResponse> categoryStats;
    @Schema(description = "성공 확률 (%)", example = "85.5")
    private double successRate;
    @Schema(description = "남은 기간 (일 단위)", example = "180")
    private long changedDays;
    @Schema(description = "지연 여부 (예상 종료일이 목표일보다 늦는지 여부)", example = "false")
    private boolean isDelayed;
}
