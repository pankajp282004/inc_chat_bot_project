package com.inc.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IncChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(IncChatbotApplication.class, args);
    }
}
