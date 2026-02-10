package com.team2.fabackend.api.analysis;

import com.team2.fabackend.api.analysis.dto.PersonalAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final com.team2.fabackend.service.analysis.AnalysisService analysisService;

    @GetMapping("/pattern/{userId}")
    public PersonalAnalysisResponse getPersonalAnalysis(@PathVariable Long userId) {
        return analysisService.getPersonalAnalysis(userId);
    }
}
