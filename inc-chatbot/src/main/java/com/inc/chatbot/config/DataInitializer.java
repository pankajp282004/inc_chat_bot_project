package com.inc.chatbot.config;

import com.inc.chatbot.entity.User;
import com.inc.chatbot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            createUserIfNotExists(userRepository, passwordEncoder, "admin", "admin123", "admin@paynways.com", User.UserRole.ADMIN);
            createUserIfNotExists(userRepository, passwordEncoder, "user1", "user123", "user1@paynways.com", User.UserRole.CONTRIBUTOR);
            createUserIfNotExists(userRepository, passwordEncoder, "viewer1", "viewer123", "viewer1@paynways.com", User.UserRole.VIEWER);
            createUserIfNotExists(userRepository, passwordEncoder, "approver1", "admin123", "approver@paynways.com", User.UserRole.APPROVER);
        };
    }

    private void createUserIfNotExists(UserRepository repo, PasswordEncoder encoder,
                                       String username, String rawPassword, String email, User.UserRole role) {
        repo.findByUsername(username).ifPresentOrElse(
                user -> {
                    // Update password to ensure it matches
                    user.setPassword(encoder.encode(rawPassword));
                    repo.save(user);
                    log.info("Updated password for user: {}", username);
                },
                () -> {
                    User user = User.builder()
                            .username(username)
                            .password(encoder.encode(rawPassword))
                            .email(email)
                            .role(role)
                            .isActive(true)
                            .build();
                    repo.save(user);
                    log.info("Created user: {} with role {}", username, role);
                }
        );
    }
}
