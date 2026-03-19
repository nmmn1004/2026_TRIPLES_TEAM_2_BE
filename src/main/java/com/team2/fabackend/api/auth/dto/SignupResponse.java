package com.team2.fabackend.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "회원가입 응답 정보")
public class SignupResponse {
    @Schema(description = "생성된 사용자 고유 식별자", example = "1", nullable = false)
    private Long userId;
}
