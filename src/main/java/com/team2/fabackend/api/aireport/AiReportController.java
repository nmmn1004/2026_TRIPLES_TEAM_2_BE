package com.team2.fabackend.api.aireport;

import com.team2.fabackend.api.aireport.dto.AiReportRequest;
import com.team2.fabackend.api.aireport.dto.AiReportResponse;
import com.team2.fabackend.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class AiReportController {
    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<AiReportResponse> sendAiReport(
            @AuthenticationPrincipal Long userId,
            @RequestBody AiReportRequest request
    ) {
        return ResponseEntity.ok(mailService.sendAiReport(userId, request.getReceiverEmail()));
    }
}
