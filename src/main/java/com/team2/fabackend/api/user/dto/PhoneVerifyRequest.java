package com.team2.fabackend.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class PhoneVerifyRequest {
    @Schema(description = "전화번호", example = "01012341234")
    private String phoneNumber;
    @Schema(description = "6자리 인증 코드", example = "000000")
    private String code;
}
