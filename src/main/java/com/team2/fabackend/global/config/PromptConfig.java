package com.team2.fabackend.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Slf4j
@Configuration
public class PromptConfig {
    @Bean
    public PromptTemplate generateAdviceSystemPrompt(ResourceLoader loader) throws IOException {
        Resource resource = loader.getResource("classpath:prompts/advice/generateAdviceSystem.st");
        String content;
        try (InputStream inputStream = resource.getInputStream()) {
            content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        return new PromptTemplate(content);
    }

    @Bean
    public PromptTemplate generateAdvicePrompt(ResourceLoader loader) throws IOException {
        Resource resource = loader.getResource("classpath:prompts/advice/generateAdvice.st");
        String content;
        try (InputStream inputStream = resource.getInputStream()) {
            content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        return new PromptTemplate(content);
    }

    @Bean
    public PromptTemplate generateAiReportSystemPrompt(ResourceLoader loader) throws IOException {
        Resource resource = loader.getResource("classpath:prompts/aiReport/generateAiReportSystem.st");
        String content;
        try (InputStream inputStream = resource.getInputStream()) {
            content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        return new PromptTemplate(content);
    }

    @Bean
    public PromptTemplate generateAiReportPrompt(ResourceLoader loader) throws IOException {
        Resource resource = loader.getResource("classpath:prompts/aiReport/generateAiReport.st");
        String content;
        try (InputStream inputStream = resource.getInputStream()) {
            content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        return new PromptTemplate(content);
    }
}