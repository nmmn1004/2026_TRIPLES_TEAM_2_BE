package com.team2.fabackend.api.user.dto;

import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.global.enums.SocialType;
import com.team2.fabackend.global.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "회원 정보 조회 응답 정보")
public class UserInfoResponse {
    @Schema(description = "사용자 고유 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "소셜 로그인 타입 (KAKAO, NAVER 등)", example = "NONE")
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Schema(description = "사용자 닉네임", example = "무말랭이")
    private String nickName;

    @Schema(description = "사용자 생년월일", example = "2002-04-01")
    private LocalDate birth;

    @Schema(description = "사용자 권한 (USER, ADMIN)", example = "USER")
    private UserType userType;

    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .socialType(user.getSocialType())
                .nickName(user.getNickName())
                .birth(user.getBirth())
                .userType(user.getUserType())
                .build();
    }
}
