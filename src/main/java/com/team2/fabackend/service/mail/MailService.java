package com.team2.fabackend.service.mail;

import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.exception.CustomException;
import com.team2.fabackend.service.user.UserReader;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;

    private final UserReader userReader;

    private final ChatClient chatClient;
    private final PromptTemplate generateAdvicePrompt;
    private final PromptTemplate generateAdviceSystemPrompt;

    public void sendAiReport(Long userId, String receiverEmail) {
        User user = userReader.findById(userId);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            mimeMessageHelper.setSubject(user.getNickName() + "님의 AI 소비 분석 리포트");



            mimeMessageHelper.setTo(receiverEmail);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILD);
        }


    }


    private String generateAiReport(Long userId) {
        return null;
    }
}
