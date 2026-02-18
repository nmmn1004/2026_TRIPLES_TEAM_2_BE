package com.team2.fabackend.api.advice.dto;

import com.team2.fabackend.global.enums.ChipmunkStatus;
import com.team2.fabackend.global.enums.ResponseStatus;

import java.util.List;

public record AdviceMessageResponse(
        ResponseStatus responseStatus,
        ChipmunkStatus chipmunkStatus,

        String message,      // 메인 메시지

        List<String> highlights  // 프론트 강조용 문장 분리
) {}
