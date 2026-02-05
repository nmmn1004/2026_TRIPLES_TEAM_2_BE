package com.team2.fabackend.api.phone.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PhoneSendRequest {
    @Schema(description = "전화번호", example = "01012341234")
    @NotBlank
    private String phoneNumber;
}
