package com.example.cumock.controller;

import com.example.cumock.dto.auth.UserResponse;
import com.example.cumock.model.User;
import com.example.cumock.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername()
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String username) {
        List<User> users = userService.searchUsersByUsername(username);
        List<UserResponse> userResponses = users.stream()
                .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getUsername()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }
}
