package com.team2.fabackend.api.ledger.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team2.fabackend.domain.ledger.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Schema(description = "가계부 내역 추가/수정 요청 정보")
public class LedgerRequest {
    @Schema(description = "거래 금액", example = "15000", nullable = false)
    private Long amount;
    @Schema(description = "카테고리 (식비, 교통, 여가, 고정비, 저축 등)", example = "식비", nullable = false)
    private String category;
    @Schema(description = "거래 메모", example = "점심 돈까스")
    private String memo;
    @Schema(description = "거래 유형 (INCOME: 수입, EXPENDITURE: 지출)", example = "EXPENDITURE", nullable = false)
    private TransactionType type;
    @Schema(description = "거래 날짜 (yyyy-MM-dd)", example = "2026-03-19")
    private LocalDate date = LocalDate.now();

    @Schema(description = "거래 시간 (HH:mm)", example = "12:30")
    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time = LocalTime.now();
}
