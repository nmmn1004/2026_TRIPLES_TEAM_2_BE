package com.team2.fabackend.api.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "이메일 인증번호 확인 요청 정보")
public class EmailVerifyRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Schema(description = "인증번호를 받은 이메일 계정", example = "user@example.com", nullable = false)
    private String email;

    @NotBlank(message = "인증번호는 필수 입력값입니다.")
    @Schema(description = "발송된 6자리 인증번호", example = "123456", nullable = false)
    private String code;
}
