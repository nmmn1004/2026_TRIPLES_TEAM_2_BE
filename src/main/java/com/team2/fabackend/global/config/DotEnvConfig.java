package com.team2.fabackend.global.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotEnvConfig {
    /**
     * Configures and provides a Dotenv bean for loading environment variables.
     *
     * @return A Dotenv instance loaded from the project root.
     */
    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure().directory("./")
                .ignoreIfMissing()
                .load();
    }
}
