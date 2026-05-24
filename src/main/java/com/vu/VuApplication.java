package com.vu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VuApplication {

    public static void main(String[] args) {
        // Dòng này là điểm bắt đầu, kích hoạt toàn bộ hệ thống Web, Database, Security...
        SpringApplication.run(VuApplication.class, args);
    }

}