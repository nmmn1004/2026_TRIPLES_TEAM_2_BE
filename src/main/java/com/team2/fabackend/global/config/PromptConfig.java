package com.team2.fabackend.global.config;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Configuration
public class PromptConfig {

    @Value("${prompt.advice.system:classpath:prompts/advice/generateAdviceSystem.st}")
    private Resource adviceSystemResource;

    @Value("${prompt.advice.user:classpath:prompts/advice/generateAdvice.st}")
    private Resource adviceResource;

    @Value("${prompt.aireport.system:classpath:prompts/aiReport/generateAiReportSystem.st}")
    private Resource aiReportSystemResource;

    @Value("${prompt.aireport.user:classpath:prompts/aiReport/generateAiReport.st}")
    private Resource aiReportResource;

    /**
     * Creates a PromptTemplate for the advice system prompt.
     *
     * @return A PromptTemplate instance.
     * @throws IOException If the resource cannot be read.
     */
    @Bean
    public PromptTemplate generateAdviceSystemPrompt() throws IOException {
        return createPromptTemplate(adviceSystemResource);
    }

    /**
     * Creates a PromptTemplate for the advice user prompt.
     *
     * @return A PromptTemplate instance.
     * @throws IOException If the resource cannot be read.
     */
    @Bean
    public PromptTemplate generateAdvicePrompt() throws IOException {
        return createPromptTemplate(adviceResource);
    }

    /**
     * Creates a PromptTemplate for the AI report system prompt.
     *
     * @return A PromptTemplate instance.
     * @throws IOException If the resource cannot be read.
     */
    @Bean
    public PromptTemplate generateAiReportSystemPrompt() throws IOException {
        return createPromptTemplate(aiReportSystemResource);
    }

    /**
     * Creates a PromptTemplate for the AI report user prompt.
     *
     * @return A PromptTemplate instance.
     * @throws IOException If the resource cannot be read.
     */
    @Bean
    public PromptTemplate generateAiReportPrompt() throws IOException {
        return createPromptTemplate(aiReportResource);
    }

    /**
     * Helper method to create a PromptTemplate from a Resource.
     *
     * @param resource The Resource to read.
     * @return A PromptTemplate containing the resource content.
     * @throws IOException If the resource cannot be read.
     */
    private PromptTemplate createPromptTemplate(Resource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return new PromptTemplate(content);
        }
    }
}
