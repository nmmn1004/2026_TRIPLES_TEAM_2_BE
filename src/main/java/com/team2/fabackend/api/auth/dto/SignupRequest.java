package com.team2.fabackend.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SignupRequest {
//    @Email
//    @NotBlank
//    @Schema(description = "이메일", example = "test@test.com")
//    private String email;

    @NotBlank
    @Schema(description = "아이디", example = "test")
    private String userId;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    @Schema(description = "비밀번호", example = "12341234")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

    @NotBlank
    @Schema(description = "실명", example = "테스형")
    private String name;

    @NotBlank
    @Schema(description = "닉네임", example = "무말랭이")
    private String nickName;

    @NotNull
    @Schema(description = "생년", example = "2002-04-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birth;

    @NotBlank
    @Schema(description = "전화번호", example = "01012341234")
    private String phoneNumber;
}
