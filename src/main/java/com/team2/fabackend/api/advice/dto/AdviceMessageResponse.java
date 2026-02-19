package com.team2.fabackend.api.advice.dto;

import com.team2.fabackend.global.enums.ChipmunkStatus;
import com.team2.fabackend.global.enums.ResponseStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "맞춤 조언 메시지 응답")
public record AdviceMessageResponse(
        @Schema(
                description = "응답 상태 (SUCCESS: 정상 생성, EXIST: 이미 생성된 조언 재사용, ERROR: 내부 오류)",
                example = "SUCCESS"
        )
        ResponseStatus responseStatus,

        @Schema(
                description = "다람쥐(캐릭터)의 감정 상태",
                example = "CHIPMUNK_POSITIVE"
        )
        ChipmunkStatus chipmunkStatus,

        @Schema(
                description = "메인 조언 메시지",
                example = "이번 달 소비 패턴이 목표와 잘 맞아요! 현재 속도를 유지해보세요."
        )
        String message,

        @Schema(
                description = "프론트에서 강조용으로 분리된 하이라이트 문구 목록",
                example = "[\"식비 예산 여유 +15%\", \"교통비 사용량 적정\"]"
        )
        List<String> highlights
) {}
