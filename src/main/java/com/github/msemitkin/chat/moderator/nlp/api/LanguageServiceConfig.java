package com.github.msemitkin.chat.moderator.nlp.api;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v2.LanguageServiceSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class LanguageServiceConfig {

    @Bean
    public LanguageServiceSettings languageServiceSettings(
        @Value("${language.service.key.path}") String keyPath
    ) throws IOException {
        return LanguageServiceSettings.newBuilder()
            .setCredentialsProvider(() -> ServiceAccountCredentials.fromStream(new FileInputStream(keyPath)))
            .build();
    }
}
