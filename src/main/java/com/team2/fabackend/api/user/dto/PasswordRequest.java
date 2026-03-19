package com.team2.fabackend.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "비밀번호 관련 요청 정보")
public class PasswordRequest {
    @Schema(description = "비밀번호 확인 요청")
    public record Verify(
            @NotBlank(message = "현재 비밀번호를 입력해주세요.")
            @Schema(description = "현재 비밀번호", example = "password123!", nullable = false)
            String currentPassword
    ) {}

    @Schema(description = "비밀번호 변경 요청")
    public record Update(
            @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
            @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
            @Schema(description = "새로운 비밀번호 (8~20자)", example = "newpassword456@", nullable = false)
            String newPassword
    ) {}
}
