package com.team2.fabackend.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "자동 로그인 요청")
public class RefreshRequest {
    @Schema(description = "갱신토큰", example = "string")
    @NotBlank
    private String refreshToken;
}
