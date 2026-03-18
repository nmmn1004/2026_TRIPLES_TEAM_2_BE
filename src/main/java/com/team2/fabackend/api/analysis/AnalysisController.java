package com.team2.fabackend.api.analysis;

import com.team2.fabackend.api.analysis.dto.PersonalAnalysisResponse;
import com.team2.fabackend.api.error.dto.ErrorResponse;
import com.team2.fabackend.service.Analysis.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@Tag(
        name = "Analysis",
        description = """
    ## 📊 소비 분석(Analysis) API
    
    사용자의 소비 패턴을 다각도로 분석하여 시각화에 필요한 데이터를 제공합니다.
    
    ---
    
    ### ⚙️ 분석 항목
    1. **카테고리별 비중**: 이번 달 총 지출 중 각 카테고리가 차지하는 비율 (원형 그래프용)
    2. **요일별 통계**: 최근 일주일간 주요 카테고리의 요일별 지출액 (점/선 그래프용)
    3. **또래 비교**: 동일 연령대 평균 지출액 대비 나의 소비 수준 분석
    
    ### 🧩 Flutter / Retrofit 예시
    ```dart
    @GET("/api/analysis/pattern/{userId}")
    Future<PersonalAnalysisResponse> getPersonalAnalysis(@Path("userId") int userId);
    ```
    """
)
public class AnalysisController {
    private final AnalysisService analysisService;

    /**
     * 특정 사용자의 개인별 소비 패턴 분석 결과를 조회합니다.
     *
     * @param userId 소비 패턴을 분석할 사용자의 ID.
     * @return 카테고리별 사용 내역, 주간 사용 내역, 소비 비율 통계가 포함된 PersonalAnalysisResponse.
     */
    @GetMapping("/pattern/{userId}")
    @Operation(
            summary = "개인 소비 패턴 분석 조회",
            description = """
        사용자의 이번 달 지출 내역을 분석하여 그래프용 데이터와 또래 대비 통계를 반환합니다.
        
        **주요 응답 필드 설명:**
        - `categoryUsageList`: 이번 달 카테고리별 [금액, 퍼센트] (원형 그래프)
        - `categoryWeeklyUsage`: 카테고리별 [월~일] 지출 배열 (점 그래프)
        - `consumptionRatio`: 또래 평균 대비 나의 지출 비율 (예: 120.5 -> 평균보다 20.5% 더 씀)
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "분석 성공", content = @Content(schema = @Schema(implementation = PersonalAnalysisResponse.class))),
            @ApiResponse(responseCode = "404", description = "유저 정보 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PersonalAnalysisResponse getPersonalAnalysis(@PathVariable Long userId) {
        return analysisService.getPersonalAnalysis(userId);
    }
}
