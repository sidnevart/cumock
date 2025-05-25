package com.example.cumock.model;

import com.example.cumock.dto.admin.CreateTestCaseRequest;
import com.example.cumock.repository.ProblemRepository;
import com.example.cumock.repository.ProblemTestCaseRepository;
import com.example.cumock.repository.UserRepository;
import com.example.cumock.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminProblemTestCaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ProblemRepository problemRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProblemTestCaseRepository testCaseRepository;
    @Autowired private JwtService jwtService;
    @Autowired private UserRepository userRepository;

    private String jwt;
    private Long problemId;

    @BeforeEach
    void setup() {
        testCaseRepository.deleteAll();
        problemRepository.deleteAll();
        userRepository.deleteAll();

        Problem problem = new Problem();
        problem.setTitle("PvP");
        problem.setDescription("desc");
        problem.setDifficulty("medium");
        problem.setTopic("algo");
        problemId = problemRepository.save(problem).getId();

        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setUsername("admin");
        admin.setPasswordHash("123");
        admin.setRole(Role.ROLE_ADMIN);
        admin.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        userRepository.save(admin);
        jwt = jwtService.generateToken(admin.getEmail());
    }

    @Test
    void shouldAddTestCase() throws Exception {
        CreateTestCaseRequest request = new CreateTestCaseRequest();
        request.setInput("2 2");
        request.setExpectedOutput("4");
        request.setSample(true);
        request.setPvp(true);

        mockMvc.perform(post("/api/admin/problems/" + problemId + "/tests")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        List<ProblemTestCase> all = testCaseRepository.findAll();
        assertEquals(1, all.size());
        assertEquals("2 2", all.get(0).getInput());
    }

    @Test
    void shouldUpdateTestCase() throws Exception {
        Problem problem = problemRepository.findById(problemId).orElseThrow();
        ProblemTestCase testCase = new ProblemTestCase();
        testCase.setProblem(problem);
        testCase.setInput("1 1");
        testCase.setExpectedOutput("2");
        testCase.setSample(true);
        testCase.setPvp(true);


        Long testId = testCaseRepository.save(testCase).getId();

        CreateTestCaseRequest update = new CreateTestCaseRequest();
        update.setInput("2 2");
        update.setExpectedOutput("4");
        update.setSample(false);
        update.setPvp(false);


        mockMvc.perform(put("/api/admin/problems/" + problemId + "/tests/" + testId)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(">>> STATUS: " + result.getResponse().getStatus());
                    System.out.println(">>> BODY: " + result.getResponse().getContentAsString());
                });


        ProblemTestCase updated = testCaseRepository.findById(testId).orElseThrow();

        assertEquals("4", updated.getExpectedOutput().trim());


        assertEquals("2 2", updated.getInput());
        assertEquals("4", updated.getExpectedOutput());
        assertFalse(updated.isSample());
        assertFalse(updated.isPvp());
    }

    @Test
    void shouldDeleteTestCase() throws Exception {
        Problem problem = problemRepository.findById(problemId).orElseThrow();
        ProblemTestCase testCase = new ProblemTestCase();
        testCase.setProblem(problem);
        testCase.setInput("1 1");
        testCase.setExpectedOutput("2");
        testCase.setSample(true);
        testCase.setPvp(true);
        Long testId = testCaseRepository.save(testCase).getId();

        mockMvc.perform(delete("/api/admin/problems/" + problemId + "/tests/" + testId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNoContent());

        assertFalse(testCaseRepository.findById(testId).isPresent());
    }

    @Test
    void shouldReturnBadRequestIfProblemIdMismatch() throws Exception {
        Problem anotherProblem = new Problem();
        anotherProblem.setTitle("Other");
        anotherProblem.setDescription("...");
        anotherProblem.setDifficulty("easy");
        anotherProblem.setTopic("math");
        anotherProblem = problemRepository.save(anotherProblem);

        ProblemTestCase testCase = new ProblemTestCase();
        testCase.setProblem(anotherProblem);
        testCase.setInput("5 5");
        testCase.setExpectedOutput("10");
        testCase.setSample(true);
        testCase.setPvp(false);
        Long testId = testCaseRepository.save(testCase).getId();

        mockMvc.perform(delete("/api/admin/problems/" + problemId + "/tests/" + testId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isBadRequest());
    }
}

