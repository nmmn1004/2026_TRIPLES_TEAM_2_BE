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
@Schema(description = "비밀번호 재설정 요청 객체")
public class PasswordResetRequest {

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Schema(description = "사용자 아이디(userId)", example = "test")
    private String userId;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
//    @Pattern(regexp = "^010\\d{8}$", message = "올바른 전화번호 형식이 아닙니다.")
    @Schema(description = "인증 완료된 전화번호", example = "01012341234")
    private String phoneNumber;

    @NotBlank(message = "새로운 비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @Schema(description = "변경할 새로운 비밀번호", example = "123412345")
    private String newPassword;
}
