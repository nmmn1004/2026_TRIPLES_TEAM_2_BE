package com.team2.fabackend.api.term.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TermSaveRequest {
    private String title;

    @Lob
    private String content;

    private String version;

    private boolean required;
}
