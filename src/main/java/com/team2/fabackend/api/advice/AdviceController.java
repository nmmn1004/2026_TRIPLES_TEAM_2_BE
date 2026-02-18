package com.team2.fabackend.api.advice;

import com.team2.fabackend.api.advice.dto.AdviceMessageResponse;
import com.team2.fabackend.service.advice.AdviceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("advice")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdviceController {
    private final AdviceService adviceService;

    @PostMapping("/generate")
    public ResponseEntity<AdviceMessageResponse> generateAdvice(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(adviceService.generateAdvice(userId));
    }
}
