package com.team2.fabackend.api.aireport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiReportRequest {
    @Schema(
            description = "프리미엄 리포트 수령 이메일 주소",
            example = "jjj4120@gmail.com"
    )
    private String receiverEmail;
}
