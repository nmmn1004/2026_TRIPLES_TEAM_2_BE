package com.team2.fabackend.api.ledger;

import com.team2.fabackend.api.ledger.dto.LedgerRequest;
import com.team2.fabackend.service.ledger.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
@Tag(name = "Ledger", description = "가계부 API")
public class LedgerController {

    private final LedgerService ledgerService;

    @PostMapping("/add")
    @Operation(summary = "가계부 내역 저장", description = "금액, 카테고리 등 저장")
    public ResponseEntity<Void> addLedger(@RequestBody LedgerRequest request) {
        ledgerService.saveLedger(request);
        return ResponseEntity.ok().build();
    }
}