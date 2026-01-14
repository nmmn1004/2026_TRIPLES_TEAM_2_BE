package com.team2.fabackend.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    @Email
    @NotBlank
    @Schema(description = "이메일", example = "test@test.com")
    private String email;

    @NotBlank
    @Schema(description = "비밀번호", example = "1234")
    private String password;

    @NotBlank
    @Schema(description = "실명", example = "테스형")
    private String name;

    @NotBlank
    @Schema(description = "닉네임", example = "무말랭이")
    private String nickName;

    @NotNull
    @Schema(description = "생년", example = "2002")
    private Integer birthYear;

    @NotBlank
    @Schema(description = "전화번호", example = "01012341234")
    private String phoneNumber;
}
