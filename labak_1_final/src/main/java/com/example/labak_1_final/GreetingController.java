package com.example.labak_1_final;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @GetMapping("/greeting")
    public String greeting(@RequestParam(defaultValue = "Гость") String name) {
        return "Добро пожаловать, " + name + "!";
    }

    @GetMapping("/info")
    public String info() {
        return "Это мой учебный проект по Spring Boot!";
    }
}