package com.example.demo.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "app.telegram")
public class AppConfig {
    private String name;
    private String token;
    private List<String> admins;
}
