package com.team2.fabackend.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Schema(description = "회원 정보 수정 요청 정보")
public class UserInfoRequest {
    @Schema(description = "수정할 닉네임", example = "새로운무말랭이")
    private String nickName;

    @Schema(description = "수정할 생년월일 (yyyy-MM-dd)", example = "2002-04-01")
    private LocalDate birth;
}
