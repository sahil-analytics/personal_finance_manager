package com.financemanager.webapp.service;

import com.financemanager.webapp.dto.RegistrationRequest;
import com.financemanager.webapp.dto.UserDTO;

public interface UserService {
    UserDTO registerUser(RegistrationRequest registrationRequest);
    UserDTO loginUser(String email, String password); // Simple login for now
    UserDTO getUserById(Long userId);
    UserDTO updateUser(Long userId, UserDTO userDTO);
    // Optional: void deleteUser(Long userId);
}