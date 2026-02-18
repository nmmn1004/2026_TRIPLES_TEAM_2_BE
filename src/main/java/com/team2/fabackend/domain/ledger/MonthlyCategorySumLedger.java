package com.team2.fabackend.domain.ledger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyCategorySumLedger {
    private String category;
    private Long totalAmount;
}
