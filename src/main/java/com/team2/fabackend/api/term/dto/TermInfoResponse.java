package com.team2.fabackend.api.term.dto;

import com.team2.fabackend.api.user.dto.UserInfoResponse;
import com.team2.fabackend.domain.term.Term;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "유효한 약관 정보")
public class TermInfoResponse {
    @Schema(description = "약관 ID", example = "1")
    private Long id;

    @Schema(description = "약관 제목", example = "서비스 이용약관")
    private String title;

    @Schema(description = "약관 버전", example = "v2.0")
    private String version;

    @Schema(description = "필수 여부", example = "true")
    private boolean required;

    @Schema(description = "약관 내용 (HTML or Markdown)")
    private String content;

    @Schema(description = "약관 시행일")
    private LocalDate effectiveAt;

    public static TermInfoResponse from(Term term) {
        return TermInfoResponse.builder()
                .id(term.getId())
                .title(term.getTitle())
                .content(term.getContent())
                .version(term.getVersion())
                .required(term.isRequired())
                .effectiveAt(term.getCreatedAt())
                .build();
    }
}
