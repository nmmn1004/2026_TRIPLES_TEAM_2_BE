package com.team2.fabackend.api.aireport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AiReportResponse(
        @Schema(
                description = "리포트 결과",
                example = "String"
        )
        String message
) {}
