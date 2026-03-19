package com.team2.fabackend.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "로그인 응답 정보")
public class LoginResponse {
    @Schema(description = "리프레시 토큰 (Access Token 만료 시 재발급을 위해 사용)", nullable = false)
    private String refreshToken;
}
