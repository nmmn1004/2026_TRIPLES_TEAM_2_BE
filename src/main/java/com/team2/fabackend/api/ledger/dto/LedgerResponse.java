package com.team2.fabackend.api.ledger.dto;

import com.team2.fabackend.domain.ledger.Ledger;
import lombok.Getter;

@Getter
public class LedgerResponse {
    private Long id;
    private Long amount;
    private String category;
    private String memo;

    /**
     * Constructs a new LedgerResponse object based on the provided Ledger entity.
     *
     * @param ledger The Ledger entity from which to initialize the response.
     */
    public LedgerResponse(Ledger ledger) {
        this.id = ledger.getId();
        this.amount = ledger.getAmount();
        this.category = ledger.getCategory();
        this.memo = ledger.getMemo();
    }
}
