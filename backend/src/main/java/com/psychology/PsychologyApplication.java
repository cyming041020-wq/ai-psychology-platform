package com.psychology;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.psychology.auth.mapper")
public class PsychologyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PsychologyApplication.class, args);
    }
}
