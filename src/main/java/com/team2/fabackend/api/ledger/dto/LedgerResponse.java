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
     * 제공된 Ledger 엔티티를 기반으로 새로운 LedgerResponse 객체를 생성합니다.
     *
     * @param ledger 응답을 초기화할 Ledger 엔티티입니다.
     */
    public LedgerResponse(Ledger ledger) {
        this.id = ledger.getId();
        this.amount = ledger.getAmount();
        this.category = ledger.getCategory();
        this.memo = ledger.getMemo();
    }
}
