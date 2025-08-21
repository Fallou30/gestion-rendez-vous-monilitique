package com.sante.senegal.config;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.MultipartConfigElement;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Getter
@Configuration
public class FileUploadConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse("10MB"));
        factory.setMaxRequestSize(DataSize.parse("10MB"));
        return factory.createMultipartConfig();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le répertoire d'upload", e);
        }
    }
}
