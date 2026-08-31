package com.psychology.controller;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public Map<String, Object> test() {
        return Map.of(
                "status", "ok",
                "service", "psychology-backend",
                "timestamp", OffsetDateTime.now().toString());
    }
}
