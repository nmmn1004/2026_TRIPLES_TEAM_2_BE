package com.team2.fabackend.global.config;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class PromptConfig {
    @Bean
    public PromptTemplate generateAdvicePrompt(ResourceLoader loader) {
        return new PromptTemplate(
                loader.getResource("classpath:prompts/advice/generateAdvice.st")
        );
    }

    @Bean
    public PromptTemplate generateAdviceSystemPrompt(ResourceLoader loader) {
        return new PromptTemplate(
                loader.getResource("classpath:prompts/advice/generateAdviceSystem.st")
        );
    }
}
