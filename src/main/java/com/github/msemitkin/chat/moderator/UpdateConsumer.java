package com.github.msemitkin.chat.moderator;

import com.github.msemitkin.chat.moderator.nlp.api.DangerousMessageChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    private static final Logger logger = LoggerFactory.getLogger(UpdateConsumer.class);

    private final TelegramClient telegramClient;
    private final DangerousMessageChecker dangerousMessageChecker;

    public UpdateConsumer(
        TelegramClient telegramClient,
        DangerousMessageChecker dangerousMessageChecker
    ) {
        this.telegramClient = telegramClient;
        this.dangerousMessageChecker = dangerousMessageChecker;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            logger.info("Received message: {}", messageText);

            if (!(messageText.split(" ").length >= 5 && dangerousMessageChecker.isDangerous(messageText))) {
                return;
            }
            logger.info("Detected dangerous message: {}", messageText);

            long chatId = update.getMessage().getChatId();
            Long senderId = update.getMessage().getFrom().getId();

            try {
                telegramClient.execute(RestrictChatMember.builder()
                    .chatId(chatId)
                    .userId(senderId)
                    .permissions(ChatPermissions.builder()
                        .canSendMessages(false)
                        .build())
                    .build());
                telegramClient.execute(SendMessage
                    .builder()
                    .chatId(chatId)
                    .text("Banned user : " + senderId)
                    .build());
            } catch (TelegramApiException e) {
                logger.error("Failed to restrict chat member", e);
            }
        }
    }

}
