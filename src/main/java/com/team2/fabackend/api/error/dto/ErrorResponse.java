package com.team2.fabackend.api.error.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 에러 응답 객체")
public record ErrorResponse(
        @Schema(description = "에러 코드 (예: U001, T001 등)", example = "U001")
        String code,
        @Schema(description = "에러 메시지", example = "사용자를 찾을 수 없습니다.")
        String message
) {}