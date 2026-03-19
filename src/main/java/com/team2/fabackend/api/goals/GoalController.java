package com.team2.fabackend.api.goals;

import com.team2.fabackend.api.error.dto.ErrorResponse;
import com.team2.fabackend.api.goals.dto.GoalAnalysisResponse;
import com.team2.fabackend.api.goals.dto.GoalRequest;
import com.team2.fabackend.api.goals.dto.GoalResponse;
import com.team2.fabackend.domain.goals.Goal;
import com.team2.fabackend.service.goals.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Tag(
        name = "Goal",
        description = """
    ## 🎯 저축 목표(Goal) API
    
    사용자가 설정한 저축 목표를 생성하고 달성 상태를 분석합니다.
    
    ---
    
    ### 🔑 주요 특징
    - **가계부 연동**: 가계부에 기록된 '저축' 내역이 목표 달성률에 실시간 반영됩니다.
    - **AI 분석**: 현재 소비 속도로 목표일 내 달성 가능한지를 분석합니다.
    
    ### 🧩 Flutter / Retrofit 예시
    ```dart
    @RestApi(baseUrl: "https://api.com/api/goals")
    abstract class GoalApi {
      @POST("")
      Future<int> createGoal(@Body GoalRequest request, @Query("userId") int userId);
      
      @GET("/{id}/analysis")
      Future<GoalAnalysisResponse> analyzeGoal(@Path("id") int goalId);
    }
    ```
    """
)
public class GoalController {
    private final GoalService goalService;

    /**
     * 사용자의 새로운 저축 목표를 생성합니다.
     * 
     * @param request 저축 목표 생성 정보가 담긴 DTO
     * @param userId 유저 식별자
     * @return 생성된 저축 목표의 ID
     */
    @PostMapping
    @Operation(
            summary = "저축 목표 생성",
            description = "새로운 저축 목표를 생성합니다. 생성된 목표의 고유 ID가 반환됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공 (ID 반환)"),
            @ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없음")
    })
    public ResponseEntity<Long> create(@RequestBody GoalRequest request, @RequestParam @Parameter(description = "유저 ID", example = "1") Long userId) {
        Long goalId = goalService.createGoal(request, userId);
        return ResponseEntity.ok(goalId);
    }

    /**
     * 시스템에 등록된 모든 저축 목표 리스트를 조회합니다.
     * 
     * @return 저축 목표 리스트를 포함한 응답 객체
     */
    @GetMapping("/list")
    @Operation(
            summary = "전체 목표 목록 조회",
            description = "과거에 종료된 목표를 포함하여 사용자의 모든 저축 목표 리스트를 조회합니다."
    )
    public ResponseEntity<Map<String, Object>> getGoalList() {
        List<GoalResponse> data = goalService.findAllGoals();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 사용자의 현재 유효한(진행 중인) 저축 목표 리스트를 조회합니다.
     * 
     * @param userId 유저 식별자
     * @return 활성화된 저축 목표 리스트를 포함한 응답 객체
     */
    @GetMapping("/active/{userId}")
    @Operation(
            summary = "진행 중인 목표 조회",
            description = "현재 종료일이 지나지 않은 활성화된 저축 목표들만 필터링하여 조회합니다."
    )
    public ResponseEntity<Map<String, Object>> getActiveGoals(@PathVariable @Parameter(description = "유저 ID", example = "1") Long userId) {
        List<GoalResponse> data = goalService.findActiveGoals(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    /**
     * 기존 저축 목표의 정보를 수정합니다.
     * 
     * @param id 수정할 저축 목표의 식별자
     * @param request 수정할 정보가 담긴 DTO
     * @return 성공 시 200 OK
     */
    @PatchMapping("/{id}")
    @Operation(
            summary = "목표 정보 수정",
            description = "목표 금액, 마감일, 제목 등 기존 목표의 상세 정보를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 완료"),
            @ApiResponse(responseCode = "404", description = "해당 목표를 찾을 수 없음")
    })
    public ResponseEntity<Void> update(@PathVariable @Parameter(description = "목표 ID", example = "10") Long id, @RequestBody GoalRequest request) {
        goalService.updateGoal(id, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 저축 목표를 삭제합니다.
     * 
     * @param id 삭제할 저축 목표의 식별자
     * @return 성공 시 200 OK
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "목표 삭제",
            description = "저축 목표를 삭제합니다. 연동된 가계부 데이터는 삭제되지 않으나 달성도 계산에서 제외됩니다."
    )
    public ResponseEntity<Void> delete(@PathVariable @Parameter(description = "목표 ID", example = "10") Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 저축 목표의 달성도 및 진행 상태를 분석한 결과를 조회합니다.
     * 
     * @param id 분석할 저축 목표의 식별자
     * @return 분석 결과 정보가 담긴 DTO
     */
    @GetMapping("/{id}/analysis")
    @Operation(
            summary = "목표 AI 달성 분석",
            description = "해당 목표의 현재 달성 속도를 분석하여 예상 성공률과 조언 메시지를 제공합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "분석 완료"),
            @ApiResponse(responseCode = "404", description = "해당 목표를 찾을 수 없음")
    })
    public ResponseEntity<GoalAnalysisResponse> analyze(@PathVariable @Parameter(description = "목표 ID", example = "10") Long id) {
        GoalAnalysisResponse analysis = goalService.analyzeGoal(id);
        return ResponseEntity.ok(analysis);
    }
}
