package com.team2.fabackend.api.goals.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoalAnalysisResponse {
    private Long goalId;
    private String analysisMessage;
    private Long changedDays;
    private String type;
    private double successRate;
}
