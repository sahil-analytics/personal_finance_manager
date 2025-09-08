package com.financemanager.webapp.controller;

import com.financemanager.webapp.dto.LoginRequest;
import com.financemanager.webapp.dto.RegistrationRequest;
import com.financemanager.webapp.dto.UserDTO;
import com.financemanager.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allow requests from any origin (adjust for production)
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        UserDTO newUser = userService.registerUser(registrationRequest);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestBody LoginRequest loginRequest) {
        // In a real app, this would involve password checking and token generation.
        // Here, we'll just find the user by email for simplicity (UNSAFE!).
        UserDTO user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            // Consider a more specific error DTO or message
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}