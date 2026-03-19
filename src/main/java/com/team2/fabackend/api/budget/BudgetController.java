package com.team2.fabackend.api.budget;

import com.team2.fabackend.api.budget.dto.BudgetRequest;
import com.team2.fabackend.api.budget.dto.BudgetResponse;
import com.team2.fabackend.api.budget.dto.BudgetUpdateRequest;
import com.team2.fabackend.api.error.dto.ErrorResponse;
import com.team2.fabackend.domain.budget.BudgetGoal;
import com.team2.fabackend.service.budget.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    
    사용자의 소비 설문 데이터를 기반으로 권장 예산을 생성하고 관리합니다.
    
    ---
    
    ### 🔑 주요 특징
    - **자동 계산**: 4대 카테고리(식비, 교통, 여가, 고정비) 예산을 설문 기반으로 자동 제안합니다.
    - **유연한 수정**: 자동 계산된 금액을 사용자가 직접 수정할 수 있습니다.
    
    ### 🧩 Flutter / Retrofit 예시
    ```dart
    @RestApi(baseUrl: "https://api.com/api/budget")
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
            summary = "예산 설정 (설문 기반)",
            description = "설문 조사 결과를 바탕으로 AI가 권장하는 카테고리별 예산을 자동 생성합니다. 이미 예산이 있다면 갱신됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설정 완료 (Budget ID 반환)"),
            @ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없음")
    })
    public Long saveBudget(@RequestBody BudgetRequest request, @PathVariable @Parameter(description = "유저 ID", example = "1") Long userId) {
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
            summary = "현재 예산 조회",
            description = "사용자에게 설정된 4대 카테고리별 예산 금액과 전체 합계를 조회합니다."
    )
    public BudgetResponse getBudget(@PathVariable @Parameter(description = "유저 ID", example = "1") Long userId) {
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
            description = "설문 방식이 아닌, 사용자가 직접 금액을 숫자로 입력하여 수정합니다."
    )
    public Long updateAmounts(@PathVariable @Parameter(description = "유저 ID", example = "1") Long userId, @RequestBody BudgetUpdateRequest request) {
        return budgetService.updateBudgetAmounts(userId, request);
    }
}
