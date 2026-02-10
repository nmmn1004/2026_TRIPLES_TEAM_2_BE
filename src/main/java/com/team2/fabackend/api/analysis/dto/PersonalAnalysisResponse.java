package com.team2.fabackend.api.analysis.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class PersonalAnalysisResponse {
    private List<CategoryUsage> categoryUsageList; //카테고리별 통계(M)
    private Map<String, List<DailyUsage>> categoryWeeklyUsage;     //요일별 통계(W)

    @Getter
    @AllArgsConstructor
    public static class CategoryUsage {
        private String category;
        private Long totalAmount;
        private double percentage;
    }

    @Getter
    @AllArgsConstructor
    public static class DailyUsage {
        private String dayOfWeek;
        private Long totalAmount;
    }
}