package com.team2.fabackend.api.term.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AgreedTermRequest {
    @Schema(
            description = "동의한 약관 ID 목록",
            example = "[1, 2, 3]"
    )
    @NotEmpty(message = "동의한 약관이 없습니다.")
    private List<Long> agreedTermIds;
}
