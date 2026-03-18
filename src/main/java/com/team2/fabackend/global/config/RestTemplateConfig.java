package com.team2.fabackend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    /**
     * 동기식 HTTP 요청을 위한 RestTemplate 빈을 설정하고 제공합니다.
     *
     * @return 새로운 RestTemplate 인스턴스.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
