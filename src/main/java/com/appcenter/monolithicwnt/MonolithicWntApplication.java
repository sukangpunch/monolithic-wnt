package com.appcenter.monolithicwnt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class MonolithicWntApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonolithicWntApplication.class, args);
    }

}
