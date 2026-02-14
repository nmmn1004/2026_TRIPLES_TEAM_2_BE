package com.team2.fabackend.service.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdviceService {
    private final ChatClient chatClient;
    private final PromptTemplate generateAdvicePrompt;
    private final PromptTemplate generateAdviceSystemPrompt;

    public String generateAdvice(String content) throws JsonProcessingException {
        Map<String, Integer> currentSpends = new HashMap<String, Integer>();
        currentSpends.put("식비", 15000);

        Map<String, Integer> setSpends = new HashMap<String, Integer>();
        setSpends.put("식비", 20000);

        ObjectMapper mapper = new ObjectMapper();

//      String currentSpendsJson = mapper.writeValueAsString(currentSpends);
//      String setSpendsJson = mapper.writeValueAsString(setSpends);

//      변화 비율 계산식은 여기서 작성..

        return chatClient.prompt()
                .system(generateAdviceSystemPrompt.getTemplate())
                .user(u -> u
                        .text(generateAdvicePrompt.getTemplate())
                        .param("content", content)
//                        .param("currentSpends", currentSpendsJson)
//                        .param("setSpends", setSpendsJson)
                )
                .call()
                .content();
    }
}
