package com.parking.service;

import com.parking.dto.Token;
import com.parking.entity.Role;
import com.parking.entity.User;
import com.parking.repository.UserRepository;
import com.parking.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public void register(String username, String password) {
        if (users.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        users.save(new User(username, encoder.encode(password), Role.USER));
    }

    public Token login(String username, String password) {
        User user = users.findByUsername(username)
                .filter(u -> encoder.matches(password, u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        return new Token(jwt.generate(user), user.getRole().name());
    }
}
