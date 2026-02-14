package com.team2.fabackend.api.ledger;

import com.team2.fabackend.api.ledger.dto.LedgerRequest;
import com.team2.fabackend.domain.ledger.Ledger;
import com.team2.fabackend.domain.user.User; // 세빈 님의 User 엔티티 경로 확인
import com.team2.fabackend.service.ledger.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 추가
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
@Tag(name = "Ledger", description = "가계부 API")
public class LedgerController {

    private final LedgerService ledgerService;

    @PostMapping("/add")
    @Operation(summary = "가계부 내역 저장", description = "로그인된 유저의 가계부 내역을 저장하고 관련 목표에 자동 반영")
    public ResponseEntity<Void> addLedger(
            @PathVariable User user,
            @RequestBody LedgerRequest request) {

        ledgerService.saveLedger(user.getId(), request); // 유저 ID 전달
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    @Operation(summary = "가계부 내역 조회", description = "현재 로그인된 유저의 모든 가계부 내역 불러오기")
    public ResponseEntity<List<Ledger>> getAllLedgers(
            @PathVariable User user) {

        List<Ledger> ledgers = ledgerService.findAllByUserId(user.getId()); // 유저별 조회
        return ResponseEntity.ok(ledgers);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "가계부 내역 수정", description = "특정 ID 가계부 내역 수정")
    public ResponseEntity<Void> updateLedger(
            @PathVariable Long id,
            @RequestBody LedgerRequest request) {

        ledgerService.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "가계부 내역 삭제", description = "특정 ID 가계부 내역 삭제")
    public ResponseEntity<Void> deleteLedger(@PathVariable Long id) {
        ledgerService.delete(id);
        return ResponseEntity.ok().build();
    }
}