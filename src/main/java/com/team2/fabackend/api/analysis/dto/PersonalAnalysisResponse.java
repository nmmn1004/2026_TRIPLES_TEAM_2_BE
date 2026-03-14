package com.team2.fabackend.api.analysis.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class PersonalAnalysisResponse {
    private List<CategoryUsage> categoryUsageList;
    private Map<String, List<DailyUsage>> categoryWeeklyUsage;

    private int ageGroup;           
    private Long averageAmount;     
    private Long myTotalSpent;      
    private double consumptionRatio; 

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
