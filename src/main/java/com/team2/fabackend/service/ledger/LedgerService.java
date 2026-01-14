package com.team2.fabackend.service.ledger;

import com.team2.fabackend.api.ledger.dto.LedgerRequest;
import com.team2.fabackend.domain.ledger.Ledger;
import com.team2.fabackend.domain.ledger.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    // 가계부 내역 저장하기
    public void saveLedger(LedgerRequest request) {
        Ledger ledger = Ledger.builder()
                .amount(request.getAmount())
                .category(request.getCategory())
                .memo(request.getMemo())
                .transactionDate(request.getTransactionDate())
                .build();

        // DB에 저장
        ledgerRepository.save(ledger);
    }
}