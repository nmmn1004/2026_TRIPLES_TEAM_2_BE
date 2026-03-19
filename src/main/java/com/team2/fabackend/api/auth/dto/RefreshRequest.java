package com.team2.fabackend.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "토큰 재발급 요청 정보")
public class RefreshRequest {
    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiJ9...", nullable = false)
    @NotBlank
    private String refreshToken;
}
