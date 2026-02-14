package com.team2.fabackend.api.goals;

import com.team2.fabackend.api.goals.dto.GoalAnalysisResponse;
import com.team2.fabackend.api.goals.dto.GoalRequest;
import com.team2.fabackend.api.goals.dto.GoalResponse;
import com.team2.fabackend.domain.goals.Goal;
import com.team2.fabackend.service.goals.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    //목표 설정/저장 (C)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody GoalRequest request, @RequestParam Long userId) {
        Long goalId = goalService.createGoal(request, userId);
        return ResponseEntity.ok(goalId);
    }

    //목표 조회 (R)
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getGoalList() {
        List<GoalResponse> data = goalService.findAllGoals();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    //목표 수정(U)
    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody GoalRequest request) {
        goalService.updateGoal(id, request);
        return ResponseEntity.ok().build();
    }

    //목표 삭제(D)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.ok().build();
    }

    //목표 달성 분석(R)
    @GetMapping("/{id}/analysis")
    public ResponseEntity<GoalAnalysisResponse> analyze(@PathVariable Long id) {
        GoalAnalysisResponse analysis = goalService.analyzeGoal(id);
        return ResponseEntity.ok(analysis);
    }
}
