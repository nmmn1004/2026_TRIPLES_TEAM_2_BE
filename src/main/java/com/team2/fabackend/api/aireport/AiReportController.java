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
    ## 📧 AI 리포트(Report) API
    
    사용자의 지출 내역을 AI가 분석하여 이메일로 상세 리포트를 전송합니다.
    
    ---
    
    ### 🔑 주요 특징
    - **이메일 전송**: 분석 결과는 PDF/HTML 형식으로 지정된 이메일로 발송됩니다.
    - **비동기 처리**: 메일 발송 요청만 즉시 응답하며, 실제 발송은 서버 백그라운드에서 처리됩니다.
    
    ### 🧩 Flutter / Retrofit 예시
    ```dart
    @RestApi(baseUrl: "https://api.com/report")
    abstract class AiReportApi {
      @POST("/send")
      Future<AiReportResponse> sendAiReport(@Body AiReportRequest request);
    }
    ```
    """
)
public class AiReportController {

    private final MailService mailService;

    /**
     * AI 기반 소비 리포트를 생성하고 지정된 수신자의 이메일로 전송합니다.
     *
     * @param userId  인증된 사용자의 ID.
     * @param request 수신자의 이메일 주소를 포함한 요청 객체.
     * @return 리포트 생성 및 전송 프로세스의 결과 메시지를 포함한 ResponseEntity.
     */
    @Operation(
            summary = "AI 리포트 이메일 발송",
            description = "이번 달 소비 분석 리포트를 사용자가 입력한 이메일 주소로 전송합니다. 설정 화면이나 리포트 탭에서 사용하세요."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "발송 요청 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 이메일 주소"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 만료)")
    })
    @PostMapping("/send")
    public ResponseEntity<AiReportResponse> sendAiReport(
            @AuthenticationPrincipal Long userId,
            @RequestBody AiReportRequest request
    ) {
        return ResponseEntity.ok(mailService.sendAiReport(userId, request.getReceiverEmail()));
    }
}
