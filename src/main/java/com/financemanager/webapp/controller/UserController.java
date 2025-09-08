package com.financemanager.webapp.controller;

import com.financemanager.webapp.dto.UserDTO;
import com.financemanager.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow requests from any origin
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint to get user profile (using userId passed in path)
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long userId) {
        UserDTO user = userService.getUserById(userId);
        // Assuming service throws ResourceNotFoundException if not found,
        // which needs a global exception handler or try-catch here for 404.
        // For simplicity now, let's assume service returns null if not found.
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint to update user profile
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUserProfile(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        // Ensure the ID in the DTO matches the path variable or ignore it
        // In a real app, security would ensure the logged-in user matches userId
        UserDTO updatedUser = userService.updateUser(userId, userDTO);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build(); // Or handle specific update errors
        }
    }

    // Note: User deletion might go here, but wasn't explicitly requested for profile mgmt.
}