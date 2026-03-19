package com.team2.fabackend.api.aireport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AI 리포트 발송 응답")
public record AiReportResponse(
        @Schema(
                description = "발송 처리 결과 메시지",
                example = "AI 리포트가 jjj4120@gmail.com으로 성공적으로 발송되었습니다."
        )
        String message
) {}
