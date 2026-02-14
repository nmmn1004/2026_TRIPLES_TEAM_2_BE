package com.team2.fabackend.api.goals.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatResponse {
    private String category; // 식비, 교통비, 여가비 등
    private Long amount;     // 해당 카테고리의 지출 합계
}
