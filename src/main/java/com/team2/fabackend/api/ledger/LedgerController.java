package com.team2.fabackend.api.ledger;

import com.team2.fabackend.api.error.dto.ErrorResponse;
import com.team2.fabackend.api.ledger.dto.LedgerRequest;
import com.team2.fabackend.api.ledger.dto.LedgerResponse;
import com.team2.fabackend.domain.ledger.Ledger;
import com.team2.fabackend.domain.user.User; 
import com.team2.fabackend.service.ledger.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; 
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
@Tag(
        name = "Ledger",
        description = """
    ## 📑 가계부(Ledger) API
    
    사용자의 지출 및 수입 내역을 기록하고 목표 달성액에 반영합니다.
    
    ---
    
    ### 🔑 주요 특징
    - **목표 자동 연동**: '저축' 카테고리로 지출을 기록하면 활성 목표 금액에 자동 합산됩니다.
    - **실시간 갱신**: 내역 수정 또는 삭제 시 연동된 목표 데이터도 즉시 재계산됩니다.
    
    ### 🧩 Flutter / Retrofit 예시
    ```dart
    @RestApi(baseUrl: "https://api.com/api/ledger")
    abstract class LedgerApi {
      @POST("/add")
      Future<void> addLedger(@Body LedgerRequest request);
      
      @GET("/list")
      Future<List<Ledger>> getLedgers();
    }
    ```
    """
)
public class LedgerController {

    private final LedgerService ledgerService;

    /**
     * 인증된 사용자의 새로운 가계부 내역을 저장하고 관련 목표에 자동으로 반영합니다.
     *
     * @param userId  인증된 사용자의 ID.
     * @param request 저장할 가계부 내역 상세 정보.
     * @return 저장 성공 시 OK 상태를 포함하는 ResponseEntity.
     */
    @PostMapping("/add")
    @Operation(
            summary = "가계부 내역 추가",
            description = "새로운 지출/수입 내역을 저장합니다. '저축' 카테고리 선택 시 활성화된 목표 금액에 반영됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "저장 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 만료 등)")
    })
    public ResponseEntity<Void> addLedger(
            @AuthenticationPrincipal Long userId,
            @RequestBody LedgerRequest request) {
        ledgerService.saveLedger(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 인증된 사용자의 모든 가계부 내역을 조회합니다.
     *
     * @param userId 인증된 사용자의 ID.
     * @return 사용자의 가계부 내역 목록을 포함하는 ResponseEntity.
     */
    @GetMapping("/list")
    @Operation(
            summary = "가계부 내역 전체 조회",
            description = "로그인한 사용자의 모든 지출/수입 내역을 리스트로 반환합니다."
    )
    public ResponseEntity<List<Ledger>> getAllLedgers(
            @AuthenticationPrincipal Long userId
    ) {
        List<Ledger> responses = ledgerService.findAllByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * ID를 통해 기존 가계부 내역을 수정하고 연결된 목표에 변경 사항을 반영합니다.
     *
     * @param id      수정할 가계부 내역의 ID.
     * @param userId  인증된 사용자의 ID.
     * @param request 수정된 가계부 내역 상세 정보.
     * @return 수정 성공 시 OK 상태를 포함하는 ResponseEntity.
     */
    @PatchMapping("/{id}")
    @Operation(
            summary = "가계부 내역 수정",
            description = "기존 내역의 금액, 날짜, 카테고리 등을 수정합니다. 금액 수정 시 연동된 목표치도 함께 변경됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 완료"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 내역을 찾을 수 없음")
    })
    public ResponseEntity<Void> updateLedger(
            @PathVariable("id") @Parameter(description = "가계부 내역 ID", example = "101") Long id,
            @AuthenticationPrincipal Long userId,
            @RequestBody LedgerRequest request
    ) {
        ledgerService.update(id, request);
        return ResponseEntity.ok().build();
    }

    /**
     * ID를 통해 특정 가계부 내역을 삭제하고 관련 목표 금액을 그에 맞춰 조정합니다.
     *
     * @param id     삭제할 가계부 내역의 ID.
     * @param userId 인증된 사용자의 ID.
     * @return 삭제 성공 시 OK 상태를 포함하는 ResponseEntity.
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "가계부 내역 삭제",
            description = "내역을 삭제합니다. '저축' 내역인 경우 목표 달성액에서 해당 금액만큼 차감됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 완료"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 내역을 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteLedger(
            @PathVariable("id") @Parameter(description = "가계부 내역 ID", example = "101") Long id,
            @AuthenticationPrincipal Long userId
    ) {
        ledgerService.delete(id);
        return ResponseEntity.ok().build();
    }
}
