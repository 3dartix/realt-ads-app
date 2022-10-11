package com.example.demo.config;

import com.example.demo.service.Telegram;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@EnableScheduling
@Slf4j
public class SpringConfig {
    @SneakyThrows
    @Bean
    public Telegram telegramBotsApi(AppConfig appConfig) {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        Telegram telegram = new Telegram(appConfig);
        telegramBotsApi.registerBot(telegram);
        log.info("телеграм бот инициализирован");
        telegram.send("тестовое сообщение!");
        return telegram;
    }
}
