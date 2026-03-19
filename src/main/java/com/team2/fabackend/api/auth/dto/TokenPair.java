package com.team2.fabackend.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "인증 토큰 세트")
public class TokenPair {
    @Schema(description = "액세스 토큰 (Bearer 타입)", nullable = false)
    private String accessToken;
    @Schema(description = "리프레시 토큰", nullable = false)
    private String refreshToken;
}
