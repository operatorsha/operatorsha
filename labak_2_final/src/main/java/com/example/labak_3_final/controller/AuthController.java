package com.example.labak_3_final.controller;

import com.example.labak_3_final.dto.RegistrationRequest;
import com.example.labak_3_final.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/csrf")
    public ResponseEntity<CsrfToken> getCsrfToken(CsrfToken token) {
        return ResponseEntity.ok(token);
    }

    @GetMapping("/register")
    public ResponseEntity<String> getRegister() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok("User registered successfully");
    }
}
