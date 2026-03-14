package com.team2.fabackend.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    /**
     * Configures and provides a ChatClient bean using the provided ChatModel.
     *
     * @param chatModel The ChatModel to be used by the ChatClient.
     * @return A configured ChatClient instance.
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
