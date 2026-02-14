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
    private Long targetAmount; //목표 금액
    private Long currentSpend; //누적 지출액
    private String status;
    public int progressRate; //진행률(목표달성률?)
    private List<CategoryStatResponse> categoryStats;
    private double successRate;  // 성공률
    private long changedDays;    // 지연 또는 단축된 날짜
    private boolean isDelayed;   // true면 지연(+), false면 단축(-)
}
