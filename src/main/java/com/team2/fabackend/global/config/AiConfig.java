package com.team2.fabackend.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    /**
     * 제공된 ChatModel을 사용하여 ChatClient 빈을 설정하고 제공합니다.
     *
     * @param chatModel ChatClient에서 사용할 ChatModel.
     * @return 설정된 ChatClient 인스턴스.
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
