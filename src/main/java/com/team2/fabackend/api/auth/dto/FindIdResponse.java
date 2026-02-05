package com.team2.fabackend.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "유저 아이디 찾기")
public class FindIdResponse {
    @Schema(description = "검열된 아이디")
    private String maskedId;
}
