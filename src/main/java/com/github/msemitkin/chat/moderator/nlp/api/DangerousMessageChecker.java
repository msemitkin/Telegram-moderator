package com.github.msemitkin.chat.moderator.nlp.api;

import com.google.cloud.language.v2.ClassificationCategory;
import com.google.cloud.language.v2.Document;
import com.google.cloud.language.v2.LanguageServiceClient;
import com.google.cloud.language.v2.LanguageServiceSettings;
import com.google.cloud.language.v2.ModerateTextResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class DangerousMessageChecker {
    private static final Logger logger = LoggerFactory.getLogger(DangerousMessageChecker.class);
    private static final Set<String> NOT_ALLOWED_TOPICS = Set.of(
        "Illicit Drugs",
        "Religion & Belief"
    );
    private static final double CONFIDENCE_THRESHOLD = 0.8;

    private final LanguageServiceSettings languageServiceSettings;

    public DangerousMessageChecker(LanguageServiceSettings languageServiceSettings) {
        this.languageServiceSettings = languageServiceSettings;
    }

    public boolean isDangerous(String text) {
        try (LanguageServiceClient languageServiceClient = LanguageServiceClient.create(languageServiceSettings)) {
            Document request = Document.newBuilder()
                .setContent(text)
                .setType(Document.Type.PLAIN_TEXT)
                .build();

            ModerateTextResponse moderateTextResponse = languageServiceClient.moderateText(request);

            List<ClassificationCategory> moderationCategories = moderateTextResponse.getModerationCategoriesList();
            boolean dangerous = moderationCategories.stream()
                .filter(category -> NOT_ALLOWED_TOPICS.contains(category.getName()))
                .anyMatch(category -> category.getConfidence() > CONFIDENCE_THRESHOLD);
            if (dangerous) {
                logger.info("Detected dangerous message: {}", moderateTextResponse);
            }
            return dangerous;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
