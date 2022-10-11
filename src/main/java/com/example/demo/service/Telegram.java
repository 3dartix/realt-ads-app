package com.example.demo.service;

import com.example.demo.config.AppConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class Telegram extends TelegramLongPollingBot {

    private final AppConfig appConfig;

    @Override
    public void onUpdateReceived(Update update) {
//        if(!update.hasMessage()){
//            return;
//        }
//
//        try {
//            final String[] emailAndPassword = update.getMessage().getText().split(",");
//            final long chatId = update.getMessage().getChatId();
//            log.info("chatId: влада $$: {}", chatId);
//        } catch (RuntimeException ex){
//            log.info("Не удалось преобразовать сообщение");
//        }
//        return;
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
    }

    @Override
    public String getBotUsername() {
        return appConfig.getName();
    }

    @Override
    public String getBotToken() {
        return appConfig.getToken();
    }

    @Override
    public void onRegister() {

    }

    public synchronized void send (String response){
        new Thread(() -> {
            if(response.equals("")){
                return;
            }

            appConfig.getAdmins().forEach(id -> {

                SendMessage message = new SendMessage();
                message.setChatId(id);
                message.setText(response);

                try {
                    this.execute(message);
                } catch (TelegramApiException e){
                    e.printStackTrace();
                }

            });
        }).start();
    }
}
