package com.psychology.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@MapperScan("com.psychology.auth.mapper")
public class MyBatisConfig {
}
