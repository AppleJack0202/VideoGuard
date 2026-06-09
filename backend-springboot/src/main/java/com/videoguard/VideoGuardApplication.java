package com.videoguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VideoGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoGuardApplication.class, args);
    }
}
