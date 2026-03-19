package com.team2.fabackend.api.advice;

import com.team2.fabackend.api.advice.dto.AdviceMessageResponse;
import com.team2.fabackend.api.error.dto.ErrorResponse;
import com.team2.fabackend.service.advice.AdviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("advice")
@RequiredArgsConstructor
@Tag(
        name = "Advice",
        description = """
    ## 🐿️ 맞춤 조언(Advice) API
    
    사용자의 예산 및 지출 내역을 분석하여 캐릭터(다람쥐)가 제공하는 오늘의 조언을 관리합니다.
    
    ---
    
    ### 🔑 주요 특징
    - **중복 방지**: 하루에 한 번만 생성하며, 이미 생성된 경우 기존 데이터를 반환합니다.
    - **감정 상태**: 조언 내용에 따라 캐릭터의 감정(ChipmunkStatus)이 함께 전달됩니다.
    
    ### 🧩 Flutter / Retrofit 예시
    ```dart
    @RestApi(baseUrl: "https://api.com/advice")
    abstract class AdviceApi {
      @POST("/generate")
      Future<AdviceMessageResponse> generateAdvice();
    }
    ```
    """
)
public class AdviceController {

    private final AdviceService adviceService;

    /**
     * 인증된 사용자를 위한 오늘의 맞춤형 소비 조언을 생성하거나 조회합니다.
     *
     * @param userId 인증된 사용자의 ID
     * @return 조언 메시지와 상태를 포함하는 ResponseEntity
     */
    @PostMapping("/generate")
    @Operation(
            summary = "오늘의 맞춤 조언 생성/조회",
            description = "로그인한 사용자의 소비 데이터를 분석하여 맞춤 조언을 생성합니다. 앱 메인 화면 진입 시 또는 조언 탭 클릭 시 호출하세요."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조언 생성/조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 만료)"),
            @ApiResponse(responseCode = "404", description = "데이터 부족으로 분석 불가")
    })
    public ResponseEntity<AdviceMessageResponse> generateAdvice(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(adviceService.generateAdvice(userId));
    }
}
