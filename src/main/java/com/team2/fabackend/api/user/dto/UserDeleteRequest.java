package com.team2.fabackend.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Schema(description = "유저 정보")
public class UserDeleteRequest {
    @Schema(description = "사유", example = "너무 어려워요")
    private String reason;

    @Schema(description = "자세한 사유", example = "조작이 불편해요")
    private String reason_detail;
}
