package com.example.cumock.controller;


import com.example.cumock.dto.problem.CreateProblemRequest;
import com.example.cumock.dto.problem.UpdateProblemRequest;
import com.example.cumock.model.Problem;
import com.example.cumock.repository.ProblemRepository;
import com.example.cumock.service.JwtService;
import com.example.cumock.model.User;
import com.example.cumock.model.Role;
import com.example.cumock.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProblemRepository problemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtService jwtService;

    private String jwt;

    @BeforeEach
    void setUp() {
        problemRepository.deleteAll();
        System.out.println(">>> Users in DB before cleanup: " + userRepository.count());
        userRepository.deleteAll();
        System.out.println(">>> Users in DB after cleanup: " + userRepository.count());


        Problem problem1 = new Problem();
        problem1.setTitle("Sum of Two");
        problem1.setDescription("Given two numbers, return their sum.");
        problem1.setDifficulty("easy");
        problem1.setTopic("math");

        Problem problem2 = new Problem();
        problem2.setTitle("Binary Tree Depth");
        problem2.setDescription("Given a binary tree, find its maximum depth.");
        problem2.setDifficulty("medium");
        problem2.setTopic("trees");


        problemRepository.saveAll(List.of(problem1, problem2));

        User admin = new User();
        String uuid = UUID.randomUUID().toString();

        admin.setEmail("admin_" + uuid + "@test.com");
        admin.setUsername("admin_" + uuid);
        admin.setPasswordHash("MTIzNAo=");
        admin.setRole(Role.ROLE_ADMIN);
        admin.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        System.out.println(">>> Saving admin user:");
        System.out.println(">>> email: " + admin.getEmail());
        System.out.println(">>> username: " + admin.getUsername());
        System.out.println(">>> role: " + admin.getRole());
        System.out.println(">>> password hash length = " + admin.getPasswordHash().length());

        try {
            userRepository.save(admin);
        } catch (Exception e) {
            e.printStackTrace(); // ✅ покажет стек
            System.out.println(">>> ERROR MESSAGE: " + e.getMessage());

            if (e.getCause() != null) {
                System.out.println(">>> CAUSE: " + e.getCause().getMessage());
            }
            if (e.getCause() != null && e.getCause().getCause() != null) {
                System.out.println(">>> ROOT CAUSE: " + e.getCause().getCause().getMessage());
            }
            throw e;
        }

        jwt = jwtService.generateToken(admin.getEmail());
    }

    @Test
    void shouldReturnProblemsForAdmin() throws Exception {
        System.out.println(">>> JWT used in test: " + jwt);

        mockMvc.perform(get("/api/admin/problems")
                        .header("Authorization", "Bearer " + jwt)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    System.out.println(">>> STATUS: " + result.getResponse().getStatus());
                    System.out.println(">>> BODY: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk());

    }

    @Test
    void shouldCreateProblemAsAdmin() throws Exception {
        CreateProblemRequest request = new CreateProblemRequest();
        request.setTitle("Created by admin");
        request.setDescription("Some desc");
        request.setDifficulty("medium");
        request.setTopic("graphs");

        mockMvc.perform(post("/api/admin/problems")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(">>> STATUS: " + result.getResponse().getStatus());
                    System.out.println(">>> BODY: " + result.getResponse().getContentAsString());
                });


        List<Problem> problems = problemRepository.findAll();
        assertEquals(3, problems.size()); // 2 добавлены в @BeforeEach
        assertTrue(problems.stream().anyMatch(p -> p.getTitle().equals("Created by admin")));
    }

    @Test
    void shouldUpdateProblemAsAdmin() throws Exception {
        Problem existing = problemRepository.findAll().get(0);

        UpdateProblemRequest request = new UpdateProblemRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setDifficulty("hard");
        request.setTopic("dp");

        mockMvc.perform(put("/api/admin/problems/" + existing.getId())
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Problem updated = problemRepository.findById(existing.getId()).orElseThrow();
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("dp", updated.getTopic());
    }


    @Test
    void shouldDeleteProblemAsAdmin() throws Exception {
        Problem existing = problemRepository.findAll().get(0);

        mockMvc.perform(delete("/api/admin/problems/" + existing.getId())
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNoContent());

        assertFalse(problemRepository.findById(existing.getId()).isPresent());
    }



}
