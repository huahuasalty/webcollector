package com.future.webcollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebcollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebcollectorApplication.class, args);
    }

}
