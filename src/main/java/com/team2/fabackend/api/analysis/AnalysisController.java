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
    
    사용자의 지출 내역을 기반으로 카테고리별, 요일별 소비 패턴을 분석합니다.
    
    ---
    
    ### 🔑 주요 특징
    - **시각화 데이터**: 원형 그래프(비중) 및 선 그래프(주간 추이)를 위한 데이터를 제공합니다.
    - **또래 비교**: 동일 연령대 평균 대비 나의 소비 수준을 분석합니다.
    
    ### 🧩 Flutter / Retrofit 예시
    ```dart
    @RestApi(baseUrl: "https://api.com/api/analysis")
    abstract class AnalysisApi {
      @GET("/pattern/{userId}")
      Future<PersonalAnalysisResponse> getPersonalAnalysis(@Path("userId") int userId);
    }
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
            description = "사용자의 이번 달 지출 데이터를 분석하여 통계 수치를 반환합니다. 분석/통계 탭 진입 시 호출하세요."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "분석 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 만료)"),
            @ApiResponse(responseCode = "404", description = "유저 정보 없음")
    })
    public PersonalAnalysisResponse getPersonalAnalysis(@PathVariable Long userId) {
        return analysisService.getPersonalAnalysis(userId);
    }
}
