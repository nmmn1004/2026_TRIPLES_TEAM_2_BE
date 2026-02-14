package com.team2.fabackend.api.goals.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class GoalRequest {
    private String title;
    private String category;
    private Long targetAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String memo;
}
