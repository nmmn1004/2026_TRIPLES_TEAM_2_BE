package com.team2.fabackend.api.aireport;

import com.team2.fabackend.api.aireport.dto.AiReportRequest;
import com.team2.fabackend.api.aireport.dto.AiReportResponse;
import com.team2.fabackend.api.error.dto.ErrorResponse;
import com.team2.fabackend.service.mail.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Tag(
        name = "AI Report",
        description = """
    ## 🧾 AI 리포트(Report) API
    
    사용자가 AI 리포트를 이메일로 받도록 요청하는 API입니다.
    
    ---
    
    ### ⚙️ 공통 요청 조건
    - 모든 `/report/**` API는 **로그인 필요**
    - `Authorization: Bearer {accessToken}` 헤더 필수
    
    ---
    
    ### 📧 요청 흐름
    1️⃣ 로그인된 사용자가 수신자 이메일을 입력  
    2️⃣ 서버가 내부적으로 AI 분석 리포트를 생성하고 이메일로 발송  
    3️⃣ **메일 발송 결과 메시지만 JSON으로 반환**  
       (※ 리포트 파일이나 인증 코드는 반환하지 않습니다.)
    """
)
public class AiReportController {

    private final MailService mailService;

    @Operation(
            summary = "AI 리포트 메일 발송",
            description = """
        로그인한 사용자의 AI 리포트를 지정된 이메일 주소로 전송합니다.
        
        내부 동작:
        - 유효한 사용자 ID를 기반으로 사용자 데이터를 조회  
        - AI 분석 결과를 PDF 또는 HTML 형식으로 생성 (서버 내부 처리)  
        - `receiverEmail`로 발송  
        - **클라이언트에는 발송 결과 메시지만 반환**합니다.  
          (리포트 파일이나 코드 반환 없음)
        
        ---
        
        ### 🚀 요청 예시
        ```json
        {
          "receiverEmail": "example@gmail.com"
        }
        ```
        
        ### 📦 응답 예시 (성공)
        ```json
        {
          "message": "AI 리포트 내용"
        }
        ```
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "메일 발송 요청 성공 (responseStatus = SUCCESS or ERROR)",
                    content = @Content(schema = @Schema(implementation = AiReportResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터 (receiverEmail이 유효하지 않음)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (토큰 만료 또는 유효하지 않음)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 (메일 서버 장애 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/send")
    public ResponseEntity<AiReportResponse> sendAiReport(
            @AuthenticationPrincipal Long userId,
            @RequestBody AiReportRequest request
    ) {
        return ResponseEntity.ok(mailService.sendAiReport(userId, request.getReceiverEmail()));
    }
}
