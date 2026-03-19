package com.team2.fabackend.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청 정보")
public class LoginRequest {
    @Schema(description = "사용자 이메일 계정", example = "user@example.com", nullable = false)
    @NotBlank
    private String email;

    @Schema(description = "사용자 비밀번호", example = "password123!", nullable = false)
    @NotBlank
    private String password;
}
