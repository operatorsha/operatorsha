package com.example.labak_3_final;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Labak3FinalApplication {

    static {
        io.github.cdimascio.dotenv.Dotenv dotenv =
                io.github.cdimascio.dotenv.Dotenv
                        .configure()
                        .ignoreIfMissing()
                        .load();

        System.setProperty("DB_USER", dotenv.get("DB_USER", ""));
        System.setProperty("DB_PASS", dotenv.get("DB_PASS", ""));
    }

    public static void main(String[] args) {
        SpringApplication.run(Labak3FinalApplication.class, args);
    }
}