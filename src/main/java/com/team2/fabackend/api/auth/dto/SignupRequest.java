package com.team2.fabackend.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "회원가입 요청 정보")
public class SignupRequest {
    @NotBlank
    @Email
    @Schema(description = "사용자 이메일 계정 (로그인 ID로 사용)", example = "user@example.com", nullable = false)
    private String email;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    @Schema(description = "사용자 비밀번호 (8자 이상 20자 이하)", example = "password123!", nullable = false)
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

    @Schema(description = "사용자 닉네임", example = "무말랭이", nullable = false)
    private String nickName;

    @NotNull
    @Schema(description = "사용자 생년월일 (ISO 8601 형식: yyyy-MM-dd)", example = "2002-04-01", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birth;

    @NotNull
    @Schema(description = "기기 식별 번호 (FCM 토큰 또는 고유 기기 ID)", example = "device-uuid-1234", nullable = false)
    private String deviceId;
}
