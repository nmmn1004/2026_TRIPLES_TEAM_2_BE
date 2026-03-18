package com.team2.fabackend.global.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotEnvConfig {
    /**
     * 환경 변수 로드를 위한 Dotenv 빈을 설정하고 제공합니다.
     *
     * @return 프로젝트 루트에서 로드된 Dotenv 인스턴스.
     */
    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure().directory("./")
                .ignoreIfMissing()
                .load();
    }
}
