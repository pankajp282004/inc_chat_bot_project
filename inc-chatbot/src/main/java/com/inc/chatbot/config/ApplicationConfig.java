package com.inc.chatbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    @Value("${app.file-storage.path:./uploads}")
    private String fileStoragePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded files
        Path uploadDir = Paths.get(fileStoragePath).toAbsolutePath();
        String uploadPath = uploadDir.toUri().toString();

        registry.addResourceHandler("/files/**")
                .addResourceLocations(uploadPath);

        // Static resources
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
