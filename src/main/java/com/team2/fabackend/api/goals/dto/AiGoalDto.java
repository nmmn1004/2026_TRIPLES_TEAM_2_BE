package com.team2.fabackend.api.goals.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AiGoalDto {
    private String title;
    private Long targetAmount;
    private LocalDate endDate;
}
