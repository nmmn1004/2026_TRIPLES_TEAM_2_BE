package com.team2.fabackend.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청")
public class LoginRequest {
    @Schema(description = "이메일", example = "test@test.com")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "비밀번호", example = "1234")
    @NotBlank
    private String password;
}
