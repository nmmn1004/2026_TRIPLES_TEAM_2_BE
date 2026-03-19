package com.team2.fabackend.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 재설정 요청 정보")
public class PasswordResetRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Schema(description = "사용자 이메일 계정", example = "user@example.com", nullable = false)
    private String email;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
//    @Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
    @Schema(description = "인증 완료된 휴대폰 번호", example = "01012345678", nullable = false)
    private String phoneNumber;

    @NotBlank(message = "새로운 비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @Schema(description = "변경할 신규 비밀번호 (8자 이상 20자 이하)", example = "newpassword123!", nullable = false)
    private String newPassword;
}
