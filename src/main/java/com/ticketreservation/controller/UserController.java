package com.ticketreservation.controller;

import com.ticketreservation.model.User;
import com.ticketreservation.repository.UserRepository;

import java.util.List;
import java.util.Locale;

public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        return user;
    }

    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
        return user;
    }

    public User createUser(User user) {
        validateUser(user);
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        if (user.getId() <= 0) {
            throw new IllegalArgumentException("User ID must be positive for update");
        }
        validateUser(user);
        return userRepository.update(user);
    }

    public void deleteUser(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("User ID must be positive for deletion");
        }
        userRepository.delete(id);
    }

    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password must not be empty");
        }
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("User account is disabled");
        }
        return user;
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        String role = user.getRole().toUpperCase(Locale.ROOT);
        if (!role.equals("ORGANIZER") && !role.equals("CUSTOMER")) {
            throw new IllegalArgumentException("Role must be ORGANIZER or CUSTOMER");
        }
    }
}
