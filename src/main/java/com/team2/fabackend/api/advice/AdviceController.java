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
    ## 📌 맞춤 조언(Advice) API
    
    사용자의 예산 설정, 카테고리별 소비 내역을 기반으로  
    **오늘의 맞춤 조언 메시지**를 생성해 반환합니다.
    
    ---
    
    ### ⚙️ 공통 요청 조건
    - 모든 `/advice/**` API는 **로그인 필요**
    - `Authorization: Bearer {accessToken}` 헤더 필수
    
    ---
    
    ### 🧩 Retrofit 예시 (AOS)
    ```kotlin
    interface AdviceApi {
        @POST("/advice/generate")
        suspend fun generateAdvice(): AdviceMessageResponse
    }
    ```
    
    - Request Body 없음
    - Access Token으로 사용자 식별 후 서버에서 분석 및 조언 생성
    """
)
public class AdviceController {

    private final AdviceService adviceService;

    @PostMapping("/generate")
    @Operation(
            summary = "맞춤 조언 생성",
            description = """
        로그인한 사용자의 예산 목표, 소비 내역, 패턴을 분석하여  
        **오늘의 맞춤 조언 메시지**를 생성합니다.
        
        내부 동작:
        - 같은 날짜에 이미 생성된 조언이 있으면 DB에서 재사용 (responseStatus = EXIST)
        - 새로 생성에 성공하면 responseStatus = SUCCESS
        - 내부 예외 발생 시 responseStatus = ERROR로 fallback 메시지 반환
        
        ---
        
        ### 🚀 요청 예시 (Kotlin / Retrofit)
        ```kotlin
        val response = adviceApi.generateAdvice()
        when (response.responseStatus) {
            ResponseStatus.SUCCESS,
            ResponseStatus.EXIST -> {
                // 정상 케이스: message, highlights, chipmunkStatus 사용
            }
            ResponseStatus.ERROR -> {
                // 오류 문구를 그대로 노출하거나, 공통 에러 UI로 연결
            }
        }
        ```
        
        ### 📦 응답 예시 (성공)
        ```json
        {
          "responseStatus": "SUCCESS",
          "chipmunkStatus": "CHIPMUNK_POSITIVE",
          "message": "이번 달 소비 패턴이 목표와 잘 맞아요! 현재 속도를 유지해보세요.",
          "highlights": [
            "식비 예산 여유 +15%",
            "교통비 사용량 적정"
          ]
        }
        ```
        
        ### 📦 응답 예시 (이미 생성된 조언 재사용)
        ```json
        {
          "responseStatus": "EXIST",
          "chipmunkStatus": "CHIPMUNK_POSITIVE",
          "message": "이미 오늘의 조언이 생성되어 있어요. 계속 이 방향으로 가볼까요?",
          "highlights": []
        }
        ```
        
        ### 📦 응답 예시 (내부 오류로 인한 fallback)
        ```json
        {
          "responseStatus": "ERROR",
          "chipmunkStatus": "CHIPMUNK_NEGATIVE",
          "message": "소비 분석 중 오류가 발생했어요. 잠시 후 다시 시도해주세요.",
          "highlights": [
            "분석 실패",
            "잠시 후 재시도"
          ]
        }
        ```
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
            맞춤 조언 생성 / 조회 성공  
            - responseStatus = SUCCESS : 새로 생성  
            - responseStatus = EXIST   : 오늘 생성된 조언 재사용  
            - responseStatus = ERROR   : 내부 오류로 인한 fallback 메시지
            """,
                    content = @Content(schema = @Schema(implementation = AdviceMessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (토큰 만료/유효하지 않음 등) → AOS: 저장된 토큰 삭제 후 로그인 화면으로 이동",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 예산/소비 데이터 미존재 (U001 또는 S003 등 사용)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 (S001) - 전역 예외 처리에서 내려주는 공통 에러 응답",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AdviceMessageResponse> generateAdvice(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(adviceService.generateAdvice(userId));
    }
}
