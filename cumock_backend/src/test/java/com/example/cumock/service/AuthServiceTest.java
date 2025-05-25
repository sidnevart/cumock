package com.example.cumock.service;


import com.example.cumock.dto.auth.RegisterRequest;
import com.example.cumock.exception.UserAlreadyExistsException;
import com.example.cumock.model.User;
import com.example.cumock.model.Role;
import com.example.cumock.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // Initialize mocks and inject them into the authService instance
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserSuccessfully(){
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123456");
        request.setUsername("testuser");

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hashed");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User saved = userCaptor.getValue();
        assertEquals("testuser", saved.getUsername());
        assertEquals("test@mail.com", saved.getEmail());
        assertEquals("hashed", saved.getPasswordHash());
        assertEquals(Role.ROLE_USER, saved.getRole());
    }

    @Test
    void shouldThrowIfEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.com");
        request.setUsername("testuser");

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
    }
}
