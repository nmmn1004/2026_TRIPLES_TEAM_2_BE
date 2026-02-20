package com.team2.fabackend.api.ledger.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team2.fabackend.domain.ledger.TransactionType;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class LedgerRequest {
    private Long amount;
    private String category;
    private String memo;
    private TransactionType type;
    private LocalDate date = LocalDate.now();

    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time = LocalTime.now();
}
