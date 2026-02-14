package com.team2.fabackend.api.goals.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoalAnalysisResponse {
    private Long goalId;
    private String analysisMessage; //분석 결과 메세지
    private Long changedDays; //변동 목표 일수
    private String type;
    private double successRate;
}
