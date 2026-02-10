package com.team2.fabackend.api.term;

import com.team2.fabackend.api.term.dto.AgreedTermRequest;
import com.team2.fabackend.api.term.dto.TermInfoResponse;
import com.team2.fabackend.api.term.dto.UserTermStatusResponse;
import com.team2.fabackend.service.userTerm.UserTermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/terms")
@RequiredArgsConstructor
public class TermController {
    private final UserTermService userTermService;

    /**
     * 현재 유효한 약관 목록 조회
     */
    @GetMapping("/active")
    public ResponseEntity<List<TermInfoResponse>> getActiveTerms()
    {
        return ResponseEntity.ok(userTermService.getActiveTerms());
    }

    /**
     * 내 약관 동의 현황 조회
     */
    @GetMapping("/me")
    public ResponseEntity<List<UserTermStatusResponse>> getUserTermStatus(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(userTermService.getUserTermStatus(userId));
    }

    /**
     * 약관 동의 처리
     */
    @PostMapping
    public ResponseEntity<Void> agreeTerms(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AgreedTermRequest request
    ) {
        userTermService.agreeTerms(userId, request.getAgreedTermIds());
        return ResponseEntity.ok().build();
    }
}
