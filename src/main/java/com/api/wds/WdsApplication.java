package com.api.wds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;

@EnableSqs
@SpringBootApplication
public class WdsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WdsApplication.class, args);
    }

}
