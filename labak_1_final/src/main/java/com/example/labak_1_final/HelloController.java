package com.example.labak_1_final;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Ку, мой первый Spring Boot контроллер!";
    }
}