package com.example.eventapp.service;

import com.example.eventapp.model.LoginRequest;
import com.example.eventapp.model.User;
import com.example.eventapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> register(User user) {
        // Debug log to verify input
        System.out.println("Registering user: " + user.getUsername() + ", " + user.getEmail());

        if (user.getEmail() == null || user.getEmail().isBlank()
                || user.getUsername() == null || user.getUsername().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }


        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    public ResponseEntity<?> login(LoginRequest login) {
        Optional<User> userOpt = userRepository.findByEmail(login.getEmail());
        if (userOpt.isPresent() && passwordEncoder.matches(login.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    public List<User> findAll() {
        return userRepository.findAll();
}
}