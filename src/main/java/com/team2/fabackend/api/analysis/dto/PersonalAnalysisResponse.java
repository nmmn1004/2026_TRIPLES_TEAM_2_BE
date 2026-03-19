package com.team2.fabackend.api.analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@Schema(description = "개인 소비 패턴 분석 응답 정보")
public class PersonalAnalysisResponse {
    @Schema(description = "카테고리별 사용량 목록 (원형 그래프용)")
    private List<CategoryUsage> categoryUsageList;
    @Schema(description = "카테고리별 주간(요일별) 지출 내역 (선/막대 그래프용)")
    private Map<String, List<DailyUsage>> categoryWeeklyUsage;

    @Schema(description = "비교 대상 연령대", example = "20")
    private int ageGroup;           
    @Schema(description = "동일 연령대 평균 지출액", example = "1500000")
    private Long averageAmount;     
    @Schema(description = "나의 이번 달 총 지출액", example = "1800000")
    private Long myTotalSpent;      
    @Schema(description = "평균 대비 소비 비율 (1.0 기준)", example = "1.2")
    private double consumptionRatio; 

    @Getter
    @AllArgsConstructor
    @Schema(description = "카테고리별 지출 통계")
    public static class CategoryUsage {
        @Schema(description = "카테고리명", example = "식비")
        private String category;
        @Schema(description = "해당 카테고리 총 지출액", example = "450000")
        private Long totalAmount;
        @Schema(description = "전체 지출 중 비율 (%)", example = "25.0")
        private double percentage;
    }

    @Getter
    @AllArgsConstructor
    @Schema(description = "요일별 지출 통계")
    public static class DailyUsage {
        @Schema(description = "요일", example = "MONDAY")
        private String dayOfWeek;
        @Schema(description = "해당 요일의 지출 합계", example = "12000")
        private Long totalAmount;
    }
}
