package com.parking.controller;

import com.parking.dto.Token;
import com.parking.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public record Credentials(@NotBlank String username, @NotBlank @Size(min = 6, message = "Password size should be at least 6 characters!") String password) { }

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody Credentials c) {
        auth.register(c.username(), c.password());
    }

    @PostMapping("/login")
    public Token login(@Valid @RequestBody Credentials c) {
        return auth.login(c.username(), c.password());
    }
}
