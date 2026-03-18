package com.team2.fabackend.api.goals.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatResponse {
    private String category;
    private Long amount;
}
