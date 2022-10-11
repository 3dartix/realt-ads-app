package com.example.demo.controller;

import com.example.demo.service.JsopService;
import com.example.demo.service.Telegram;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdsController {
    private final JsopService jsopService;
    private final Telegram telegram;

    @GetMapping("/start")
    public List<String> start(){
        return jsopService.start();
    }

    @PostMapping("/getAllAdsByData")
    @Operation(summary = "Get a book by its id")
    List<String> getAllAdsByData(@RequestBody Data data){
        log.info("data: {}", data);
        List<String> allAdsByData = jsopService.getAllAdsByData(data.getStart());

        telegram.send("\n****\n");
        log.info("start task...");

        if(allAdsByData.isEmpty()) {
            telegram.send("новых объявлений нет");
        }

        allAdsByData.forEach(ad -> {
            telegram.send(ad);
            log.info("send ad: {}", ad);
        });
        log.info("end task... result: {}", allAdsByData);

        return allAdsByData;
    }

    @lombok.Data
    public static class Data {
        private LocalDateTime start;
        private LocalDateTime end;
    }
}
