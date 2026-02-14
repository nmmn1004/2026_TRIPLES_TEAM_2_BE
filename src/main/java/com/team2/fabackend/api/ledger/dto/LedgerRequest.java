package com.team2.fabackend.api.ledger.dto;

import com.team2.fabackend.domain.ledger.TransactionType;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class LedgerRequest {
    private Long amount;
    private String category;
    private String memo;
    private TransactionType type; //수입 지출 이체 선택
    private LocalDate date = LocalDate.now();
    private LocalTime time = LocalTime.now();
}