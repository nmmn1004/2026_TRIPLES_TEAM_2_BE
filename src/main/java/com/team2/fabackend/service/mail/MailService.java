package com.team2.fabackend.service.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team2.fabackend.api.aireport.dto.AiReportResponse;
import com.team2.fabackend.api.budget.dto.AiBudgetGoalDto;
import com.team2.fabackend.api.goals.dto.AiGoalDto;
import com.team2.fabackend.domain.budget.BudgetGoal;
import com.team2.fabackend.domain.goals.Goal;
import com.team2.fabackend.domain.goals.GoalRepository;
import com.team2.fabackend.domain.ledger.MonthlyLedgerDetailResponse;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.enums.UserType;
import com.team2.fabackend.global.exception.CustomException;
import com.team2.fabackend.service.budget.BudgetReader;
import com.team2.fabackend.service.ledger.LedgerReader;
import com.team2.fabackend.service.user.UserReader;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;

    private final UserReader userReader;

    private final BudgetReader budgetReader;
    private final LedgerReader ledgerReader;
    private final GoalRepository goalRepository;

    private final ChatClient chatClient;
    private final PromptTemplate generateAiReportPrompt;
    private final PromptTemplate generateAiReportSystemPrompt;

    /**
     * 사용자를 위한 AI 리포트를 생성하고 지정된 수신자 이메일로 전송합니다.
     *
     * @param userId        사용자의 ID.
     * @param receiverEmail 수신자의 이메일 주소.
     * @return 생성된 리포트 내용을 포함하는 AiReportResponse.
     */
    public AiReportResponse sendAiReport(Long userId, String receiverEmail) {
        User user = userReader.findById(userId);

        if (user.getUserType() != UserType.ADMIN) {
            throw new CustomException(ErrorCode.INSUFFICIENT_ADMIN_AUTHORITY);
        }

        String message = generateAiReport(userId);

        sendMail(receiverEmail, user.getNickName() + "님의 AI 소비 분석 리포트", message);

        return new AiReportResponse(message);
    }

    /**
     * 지정된 수신자에게 이메일을 전송합니다.
     *
     * @param to      수신자 이메일 주소.
     * @param subject 이메일 제목.
     * @param content 이메일 본문 (HTML).
     */
    public void sendMail(String to, String subject, String content) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setFrom("jjj4120@gmail.com"); // 발신자 설정 (환경 변수 권장)
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }


    /**
     * LLM과 사용자 데이터를 사용하여 AI 소비 리포트 생성을 조율합니다.
     *
     * @param userId 사용자의 ID.
     * @return 생성된 AI 리포트 문자열.
     */
    private String generateAiReport(Long userId) {
        String userNickName = userReader.findById(userId).getNickName();

        int maxRetries = 3;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                BudgetGoal budgetGoal = budgetReader.getById(userId);

                AiBudgetGoalDto aiBudget = new AiBudgetGoalDto(
                        budgetGoal.getFoodAmount(),
                        budgetGoal.getTransportAmount(),
                        budgetGoal.getLeisureAmount(),
                        budgetGoal.getFixedAmount(),
                        budgetGoal.getTotalAmount()
                );

                List<Goal> goals = goalRepository.findAllByUser_Id(userId);

                List<AiGoalDto> aiGoals = goals.stream()
                        .map(goal -> new AiGoalDto(
                                goal.getTitle(),
                                goal.getTargetAmount(),
                                goal.getEndDate()
                        ))
                        .filter(g -> StringUtils.hasText(g.getTitle()))
                        .toList();

                List<MonthlyLedgerDetailResponse> monthlyDetails = ledgerReader.getMonthlyLedgerDetails(userId);

                ObjectMapper mapper = JsonMapper.builder()
                        .addModule(new JavaTimeModule())
                        .build();

                String budgetGoalJson = mapper.writeValueAsString(aiBudget);
                String goalsJson = mapper.writeValueAsString(aiGoals);
                String monthlyDetailsJson = mapper.writeValueAsString(monthlyDetails);

                String message = chatClient.prompt()
                        .system(generateAiReportSystemPrompt.getTemplate())
                        .user(u -> u
                                .text(generateAiReportPrompt.getTemplate())
                                .param("userNickName", userNickName == null ? "고객" : userNickName)
                                .param("budgetGoalJson", budgetGoalJson == null ? "{}" : budgetGoalJson)
                                .param("goalsJson", goalsJson == null ? "[]" : goalsJson)
                                .param("monthlyDetailsJson", monthlyDetailsJson == null ? "[]" : monthlyDetailsJson)
                        )
                        .call()
                        .content();

                message = message
                        .replaceAll("(?s)```html", "")
                        .replaceAll("```", "")
                        .trim();

                return message;

            } catch (Exception e) {
                if (attempt == maxRetries) {
                    throw new CustomException(ErrorCode.AI_REPORT_GENERATION_FAILED);
                }

                try {
                    Thread.sleep(1000L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        }

        throw new CustomException(ErrorCode.AI_REPORT_GENERATION_FAILED);
    }
}
