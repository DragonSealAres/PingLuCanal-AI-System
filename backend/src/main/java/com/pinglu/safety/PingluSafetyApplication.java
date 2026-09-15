package com.pinglu.safety;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.pinglu.safety.mapper")
@SpringBootApplication
public class PingluSafetyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PingluSafetyApplication.class, args);
    }
}
