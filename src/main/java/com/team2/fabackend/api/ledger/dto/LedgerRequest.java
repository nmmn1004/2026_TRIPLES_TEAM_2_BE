package com.team2.fabackend.api.ledger.dto;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class LedgerRequest {
    private Long amount;
    private String category;
    private String memo;
    private LocalDateTime transactionDate;
}