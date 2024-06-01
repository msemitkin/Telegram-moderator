package com.github.msemitkin.chat.moderator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

@Component
public class TelegramBot implements SpringLongPollingBot {
    private final String token;
    private final LongPollingUpdateConsumer updateConsumer;

    public TelegramBot(
        @Value("${telegram.bot.token}") String token,
        UpdateConsumer updateConsumer
    ) {
        this.token = token;
        this.updateConsumer = updateConsumer;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateConsumer;
    }
}
