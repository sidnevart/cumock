package com.example.cumock.controller;

import com.example.cumock.dto.auth.UserResponse;
import com.example.cumock.model.User;
import com.example.cumock.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.cumock.dto.auth.UserDTO;
import com.example.cumock.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole()
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String username) {
        List<User> users = userService.searchUsersByUsername(username);
        List<UserResponse> userResponses = users.stream()
                .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getRole()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }

    // check if admin
    @GetMapping("/is_admin")
    public ResponseEntity<Boolean> isAdmin() {
        boolean isAdmin = userService.isCurrentUserAdmin();
        return ResponseEntity.ok(isAdmin);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(user -> {
                // Create new UserDTO and set fields individually
                UserDTO userDTO = new UserDTO(); 
                userDTO.setId(user.getId());
                userDTO.setUsername(user.getUsername());
                userDTO.setEmail(user.getEmail());
                return ResponseEntity.ok(userDTO);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponse> userResponses = users.stream()
                .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getRole()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }   
}
