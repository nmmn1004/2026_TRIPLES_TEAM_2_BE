package com.team2.fabackend.api.term.dto;

import com.team2.fabackend.domain.term.Term;
import com.team2.fabackend.domain.userTerm.UserTerm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "유저 약관 동의 현황")
public class UserTermStatusResponse {
    @Schema(description = "약관 ID", example = "1")
    private Long termId;

    @Schema(description = "약관 제목", example = "서비스 이용약관")
    private String title;

    @Schema(description = "약관 버전", example = "v2.0")
    private String version;

    @Schema(description = "필수 여부", example = "true")
    private boolean required;

    @Schema(description = "동의 여부", example = "true")
    private boolean agreed;

    @Schema(description = "동의 일시", example = "2026-02-01T10:00:00")
    private LocalDateTime agreedAt;

    public static UserTermStatusResponse from(Term term, UserTerm userTerm) {
        return UserTermStatusResponse.builder()
                .termId(term.getId())
                .title(term.getTitle())
                .version(term.getVersion())
                .required(term.isRequired())
                .agreed(userTerm != null)
                .agreedAt(userTerm != null ? userTerm.getAgreedAt() : null)
                .build();
    }
}
