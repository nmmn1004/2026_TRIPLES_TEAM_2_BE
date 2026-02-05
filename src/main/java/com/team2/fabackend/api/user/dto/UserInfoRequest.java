package com.team2.fabackend.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Schema(description = "유저 정보")
public class UserInfoRequest {
    @Schema(description = "유저 실명", example = "null")
    private String name;

    @Schema(description = "유저 별명", example = "null")
    private String nickName;

    @Schema(description = "유저 생년월일", example = "null")
    private LocalDate birth;
}
