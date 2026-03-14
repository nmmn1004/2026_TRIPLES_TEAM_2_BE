package com.team2.fabackend.api.budget;

import com.team2.fabackend.api.budget.dto.BudgetRequest;
import com.team2.fabackend.api.budget.dto.BudgetResponse;
import com.team2.fabackend.api.budget.dto.BudgetUpdateRequest;
import com.team2.fabackend.api.error.dto.ErrorResponse;
import com.team2.fabackend.domain.budget.BudgetGoal;
import com.team2.fabackend.service.budget.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
@Tag(
        name = "Budget",
        description = """
    ## 💰 예산 설정(Budget) API
    
    사용자의 소비 설문 데이터를 기반으로 **카테고리별 예산 목표**를 생성, 조회, 수정합니다.
    
    ---
    
    ### ⚙️ 공통 사항
    - 모든 `/api/budget/**` API는 **로그인한 사용자 ID**가 필요합니다.
    - 초기 설문 완료 시 `POST /{userId}`를 호출하여 예산을 자동 계산 및 저장합니다.
    
    ### 🧩 Flutter / Retrofit 예시
    ```dart
    @RestApi(baseUrl: "https://your-api.com/api/budget")
    abstract class BudgetApi {
      @POST("/{userId}")
      Future<int> saveBudget(@Path("userId") int userId, @Body BudgetRequest request);
      
      @GET("/{userId}")
      Future<BudgetResponse> getBudget(@Path("userId") int userId);
    }
    ```
    """
)
public class BudgetController {
    private final BudgetService budgetService;

    /**
     * 사용자가 입력한 설문 항목을 바탕으로 카테고리별 예산을 계산하여 저장하거나 기존 예산을 업데이트합니다.
     * 
     * @param request 설문 옵션 정보가 담긴 DTO
     * @param userId 유저 식별자
     * @return 생성 또는 수정된 예산 목표의 ID
     */
    @PostMapping("/{userId}")
    @Operation(
            summary = "예산 목표 생성/업데이트 (설문 기반)",
            description = """
        사용자가 입력한 설문 항목(BudgetRequest)을 바탕으로 서버에서 4대 카테고리(식비, 교통, 여가, 고정비) 예산을 계산하여 저장합니다.
        - 이미 예산이 존재하는 경우: 기존 데이터를 업데이트합니다.
        - 예산이 없는 경우: 새로 생성합니다.
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "저장 완료 (생성된 Budget ID 반환)"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Long saveBudget(@RequestBody BudgetRequest request, @PathVariable Long userId) {
        return budgetService.saveBudget(request, userId);
    }

    /**
     * 특정 사용자의 현재 예산 목표 설정 정보를 조회합니다.
     * 
     * @param userId 유저 식별자
     * @return 카테고리별 예산 금액 정보가 담긴 응답 DTO
     */
    @GetMapping("/{userId}")
    @Operation(
            summary = "예산 목표 조회",
            description = "사용자의 현재 설정된 4대 카테고리별 예산 금액을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = BudgetResponse.class))),
            @ApiResponse(responseCode = "404", description = "설정된 예산 목표가 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public BudgetResponse getBudget(@PathVariable Long userId) {
        return budgetService.getBudget(userId);
    }

    /**
     * 사용자가 직접 입력한 금액으로 각 카테고리별 예산을 수정합니다.
     * 
     * @param userId 유저 식별자
     * @param request 수정할 카테고리별 금액 정보가 담긴 DTO
     * @return 수정된 예산 목표의 ID
     */
    @PatchMapping("/{userId}/amounts")
    @Operation(
            summary = "예산 금액 직접 수정",
            description = "설문 방식이 아닌, 사용자가 직접 각 카테고리의 예산 금액을 숫자로 입력하여 수정할 때 사용합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 완료"),
            @ApiResponse(responseCode = "404", description = "수정할 예산 데이터가 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Long updateAmounts(@PathVariable Long userId, @RequestBody BudgetUpdateRequest request) {
        return budgetService.updateBudgetAmounts(userId, request);
    }
}
