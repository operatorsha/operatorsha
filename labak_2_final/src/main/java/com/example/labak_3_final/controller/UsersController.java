package com.example.labak_3_final.controller;

import com.example.labak_3_final.model.AppUser;
import com.example.labak_3_final.repository.AppUserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final AppUserRepository userRepository;

    public UsersController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }
}
