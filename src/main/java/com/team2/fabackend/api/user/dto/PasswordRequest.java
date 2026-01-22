package com.team2.fabackend.api.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordRequest {
    public record Verify(
            @NotBlank(message = "현재 비밀번호를 입력해주세요.")
            String currentPassword
    ) {}

    public record Update(
            @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
            @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
            String newPassword
    ) {}
}
