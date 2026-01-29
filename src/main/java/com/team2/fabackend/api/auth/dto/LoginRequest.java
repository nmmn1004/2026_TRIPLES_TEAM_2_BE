package com.team2.fabackend.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청")
public class LoginRequest {
    @Schema(description = "아이디", example = "test")
    @NotBlank
    private String userId;

    @Schema(description = "비밀번호", example = "12341234")
    @NotBlank
    private String password;
}
