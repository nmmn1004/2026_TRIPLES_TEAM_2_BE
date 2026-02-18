package com.team2.fabackend.domain.ledger;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class MonthlyLedgerDetailResponse {
    private String category;
    private Long amount;
    private LocalDate date;
    private LocalTime time;
}
