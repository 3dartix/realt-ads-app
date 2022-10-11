package com.example.demo.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class MyScheduled {

    private final JsopService jsopService;
    private final Telegram telegram;

    @Scheduled(fixedDelay = 900_000)
    public void scheduleFixedDelayTask() {
        telegram.send("\n****\n");
        log.info("start task...");
        List<String> start = jsopService.start();

        if(start.isEmpty()) {
            telegram.send("новых объявлений нет");
        }

        start.forEach(ad -> {
            telegram.send(ad);
            log.info("send ad: {}", ad);
        });
        log.info("end task... result: {}", start);
    }
}
