package com.team2.fabackend.api.advice.dto;

import com.team2.fabackend.global.enums.ChipmunkStatus;
import com.team2.fabackend.global.enums.ResponseStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record AdviceMessageResponse(
        @Enumerated(EnumType.STRING)
        ResponseStatus responseStatus,

        @Enumerated(EnumType.STRING)
        ChipmunkStatus chipmunkStatus,

        String context
) {}
