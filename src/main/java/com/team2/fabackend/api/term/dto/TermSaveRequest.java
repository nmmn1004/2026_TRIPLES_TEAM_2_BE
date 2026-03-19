package com.team2.fabackend.api.term.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "[ADMIN] 약관 저장/수정 요청")
public class TermSaveRequest {
    @Schema(description = "약관 제목", example = "개인정보 처리방침")
    private String title;

    @Lob
    @Schema(description = "약관 상세 내용")
    private String content;

    @Schema(description = "약관 버전", example = "v1.0")
    private String version;

    @Schema(description = "필수 동의 여부", example = "true")
    private boolean required;
}
