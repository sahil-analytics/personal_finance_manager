package com.financemanager.webapp.service.impl;

import com.financemanager.webapp.dto.RegistrationRequest;
import com.financemanager.webapp.dto.UserDTO;
import com.financemanager.webapp.exception.ResourceNotFoundException;
import com.financemanager.webapp.model.User;
import com.financemanager.webapp.repository.UserRepository;
import com.financemanager.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Important for DB operations

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    // Simple mapper methods (Could be moved to a dedicated Mapper class)
    private UserDTO mapToUserDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getPreferredCurrency());
    }

    @Override
    @Transactional // Ensure atomicity
    public UserDTO registerUser(RegistrationRequest registrationRequest) {
        // 1. Check if email already exists
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new IllegalArgumentException("Email address already in use.");
        }

        // 2. Create User entity (NO PASSWORD HASHING as requested - INSECURE)
        User newUser = new User();
        newUser.setName(registrationRequest.getName());
        newUser.setEmail(registrationRequest.getEmail());
        newUser.setPassword(registrationRequest.getPassword()); // Store plain text password
        newUser.setPreferredCurrency(registrationRequest.getPreferredCurrency());

        // 3. Save user
        User savedUser = userRepository.save(newUser);

        // 4. Map to DTO and return
        return mapToUserDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true) // Read-only transaction for login check
    public UserDTO loginUser(String email, String password) {
        // Insecure: Plain text password check
        User user = userRepository.findByEmail(email)
                .orElse(null); // Return null if not found

        if (user != null && user.getPassword().equals(password)) {
            return mapToUserDTO(user);
        }
        return null; // Indicate failed login
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapToUserDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Update allowed fields
        existingUser.setName(userDTO.getName());
        existingUser.setPreferredCurrency(userDTO.getPreferredCurrency());
        // Avoid changing email/password here without proper verification flows

        User updatedUser = userRepository.save(existingUser);
        return mapToUserDTO(updatedUser);
    }
}