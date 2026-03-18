package com.team2.fabackend.api.goals.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GoalResponse {
    private Long id;
    private String title;
    private String category;
    private Long targetAmount;
    private Long currentSpend;
    private String status;
    public int progressRate;
    private List<CategoryStatResponse> categoryStats;
    private double successRate;
    private long changedDays;
    private boolean isDelayed;
}
