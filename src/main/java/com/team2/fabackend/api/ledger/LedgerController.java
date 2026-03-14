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
    
    사용자의 지출 및 수입 내역을 관리합니다. 모든 내역은 생성 시 자동으로 관련 **저축 목표(Goal)**의 현재 달성액에 반영됩니다.
    
    ---
    
    ### ⚙️ 주요 기능
    - **내역 저장**: 지출/수입 금액, 카테고리, 날짜를 입력합니다.
    - **자동 연동**: '저축' 카테고리로 입력된 지출은 사용자의 활성화된 목표 금액을 증가시킵니다.
    - **내역 관리**: 특정 내역의 수정 및 삭제가 가능하며, 변경 사항은 목표 금액에도 즉시 반영됩니다.
    
    ### 🧩 Flutter / Retrofit 예시
    ```dart
    @RestApi(baseUrl: "https://your-api.com/api/ledger")
    abstract class LedgerApi {
      @POST("/add")
      Future<void> addLedger(@Body LedgerRequest request);
      
      @GET("/list")
      Future<List<Ledger>> getAllLedgers();
      
      @PATCH("/{id}")
      Future<void> updateLedger(@Path("id") int id, @Body LedgerRequest request);
    }
    ```
    """
)
public class LedgerController {

    private final LedgerService ledgerService;

    /**
     * Saves a new ledger entry for the authenticated user and automatically reflects it in relevant goals.
     *
     * @param userId  The ID of the authenticated user.
     * @param request The ledger entry details to be saved.
     * @return A ResponseEntity with OK status upon successful save.
     */
    @PostMapping("/add")
    @Operation(summary = "가계부 내역 저장", description = "로그인된 유저의 가계부 내역을 저장하고 관련 목표에 자동 반영합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "저장 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> addLedger(
            @AuthenticationPrincipal Long userId,
            @RequestBody LedgerRequest request) {
        ledgerService.saveLedger(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves all ledger entries for the authenticated user.
     *
     * @param userId The ID of the authenticated user.
     * @return A ResponseEntity containing the list of ledger entries for the user.
     */
    @GetMapping("/list")
    @Operation(summary = "가계부 내역 조회", description = "현재 로그인된 유저의 모든 가계부 내역을 조회합니다.")
    public ResponseEntity<List<Ledger>> getAllLedgers(
            @AuthenticationPrincipal Long userId
    ) {
        List<Ledger> responses = ledgerService.findAllByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Updates an existing ledger entry by its ID and reflects changes in linked goals.
     *
     * @param id      The ID of the ledger entry to update.
     * @param userId  The ID of the authenticated user.
     * @param request The updated ledger entry details.
     * @return A ResponseEntity with OK status upon successful update.
     */
    @PatchMapping("/{id}")
    @Operation(summary = "가계부 내역 수정", description = "특정 ID의 가계부 내역을 수정합니다. 수정된 금액은 연동된 목표에도 반영됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "내역을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> updateLedger(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal Long userId,
            @RequestBody LedgerRequest request
    ) {
        ledgerService.update(id, request);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a specific ledger entry by its ID and adjusts relevant goal amounts accordingly.
     *
     * @param id     The ID of the ledger entry to delete.
     * @param userId The ID of the authenticated user.
     * @return A ResponseEntity with OK status upon successful deletion.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "가계부 내역 삭제", description = "특정 ID의 가계부 내역을 삭제합니다. 삭제된 금액만큼 연동된 목표 금액이 차감됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "내역을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteLedger(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal Long userId
    ) {
        ledgerService.delete(id);
        return ResponseEntity.ok().build();
    }
}
