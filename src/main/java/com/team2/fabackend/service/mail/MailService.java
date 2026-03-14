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
     * Generates an AI report for the user and sends it to the specified receiver email.
     *
     * @param userId        The ID of the user.
     * @param receiverEmail The recipient's email address.
     * @return An AiReportResponse containing the generated report content.
     */
    public AiReportResponse sendAiReport(Long userId, String receiverEmail) {
        User user = userReader.findById(userId);

        if (user.getUserType() != UserType.ADMIN) {
            throw new CustomException(ErrorCode.INSUFFICIENT_ADMIN_AUTHORITY);
        }

        String message = generateAiReport(userId);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            mimeMessageHelper.setFrom("jjj4120@gmail.com");

            mimeMessageHelper.setTo(receiverEmail);

            mimeMessageHelper.setSubject(user.getNickName() + "님의 AI 소비 분석 리포트");

            mimeMessageHelper.setText(message, true);

            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }

        return new AiReportResponse(message);
    }


    /**
     * Orchestrates the generation of an AI consumption report using LLM and user data.
     *
     * @param userId The ID of the user.
     * @return The generated AI report as a string.
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

                List<Goal> goals = goalRepository.findAllByUserId(userId);

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
