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
     * advice 시스템 프롬프트를 위한 PromptTemplate을 생성합니다.
     *
     * @return PromptTemplate 인스턴스.
     * @throws IOException 리소스를 읽을 수 없는 경우.
     */
    @Bean
    public PromptTemplate generateAdviceSystemPrompt() throws IOException {
        return createPromptTemplate(adviceSystemResource);
    }

    /**
     * advice 사용자 프롬프트를 위한 PromptTemplate을 생성합니다.
     *
     * @return PromptTemplate 인스턴스.
     * @throws IOException 리소스를 읽을 수 없는 경우.
     */
    @Bean
    public PromptTemplate generateAdvicePrompt() throws IOException {
        return createPromptTemplate(adviceResource);
    }

    /**
     * AI 리포트 시스템 프롬프트를 위한 PromptTemplate을 생성합니다.
     *
     * @return PromptTemplate 인스턴스.
     * @throws IOException 리소스를 읽을 수 없는 경우.
     */
    @Bean
    public PromptTemplate generateAiReportSystemPrompt() throws IOException {
        return createPromptTemplate(aiReportSystemResource);
    }

    /**
     * AI 리포트 사용자 프롬프트를 위한 PromptTemplate을 생성합니다.
     *
     * @return PromptTemplate 인스턴스.
     * @throws IOException 리소스를 읽을 수 없는 경우.
     */
    @Bean
    public PromptTemplate generateAiReportPrompt() throws IOException {
        return createPromptTemplate(aiReportResource);
    }

    /**
     * Resource로부터 PromptTemplate을 생성하는 헬퍼 메서드입니다.
     *
     * @param resource 읽어올 Resource.
     * @return 리소스 내용을 포함하는 PromptTemplate.
     * @throws IOException 리소스를 읽을 수 없는 경우.
     */
    private PromptTemplate createPromptTemplate(Resource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return new PromptTemplate(content);
        }
    }
}
