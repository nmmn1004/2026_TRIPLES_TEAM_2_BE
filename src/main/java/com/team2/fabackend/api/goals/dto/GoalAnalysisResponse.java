package com.team2.fabackend.api.goals.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "저축 목표 AI 분석 응답 정보")
public class GoalAnalysisResponse {
    @Schema(description = "목표 ID", example = "1")
    private Long goalId;
    @Schema(description = "AI 분석 메시지", example = "현재 소비 속도라면 목표일보다 10일 일찍 달성할 수 있어요!")
    private String analysisMessage;
    @Schema(description = "예상 종료일 차이 (일 단위)", example = "-10")
    private Long changedDays;
    @Schema(description = "분석 타입 (긍정적/부정적 등)", example = "POSITIVE")
    private String type;
    @Schema(description = "AI가 계산한 달성 가능성 (%)", example = "92.4")
    private double successRate;
}
