package com.example.cumock.controller;

import com.example.cumock.dto.code_sandbox.CodeRequest;
import com.example.cumock.dto.code_sandbox.SubmissionRequest;
import com.example.cumock.model.Problem;
import com.example.cumock.model.ProblemTestCase;
import com.example.cumock.model.Role;
import com.example.cumock.model.User;
import com.example.cumock.repository.ProblemRepository;
import com.example.cumock.repository.ProblemTestCaseRepository;
import com.example.cumock.repository.UserRepository;
import com.example.cumock.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CodeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ProblemRepository problemRepository;
    @Autowired private ProblemTestCaseRepository testCaseRepository;
    @Autowired private JwtService jwtService;
    @Autowired private ObjectMapper objectMapper;
    private String jwt;
    private Long problemId;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        testCaseRepository.deleteAll();
        problemRepository.deleteAll();
        userRepository.deleteAll();

        Problem problem = new Problem();
        problem.setTitle("Sum");
        problem.setDescription("Add 2 numbers");
        problem.setDifficulty("easy");
        problem.setTopic("math");
        problemId = problemRepository.save(problem).getId();
        ProblemTestCase testCase1 = new ProblemTestCase();
        testCase1.setProblem(problem);
        testCase1.setInput("1 2");
        testCase1.setExpectedOutput("3");
        testCase1.setSample(true);
        testCase1.setPvp(false);

        ProblemTestCase testCase2 = new ProblemTestCase();
        testCase2.setProblem(problem);
        testCase2.setInput("10 15");
        testCase2.setExpectedOutput("25");
        testCase2.setSample(true);
        testCase2.setPvp(false);


        User user = new User();
        user.setEmail("user@test.com");
        user.setUsername("user");
        user.setPasswordHash("123");
        user.setRole(Role.ROLE_USER);
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        userRepository.save(user);

        testCaseRepository.saveAll(List.of(testCase1, testCase2));

        jwt = jwtService.generateToken(user.getEmail());

    }

    @Test
    void shouldReturnResultsForSampleTests() throws Exception {
        CodeRequest request = new CodeRequest();
        request.setProblemId(problemId);
        request.setLanguage("python");
        request.setCode("print(sum(map(int, input().split())))");

        mockMvc.perform(post("/api/code/run")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results[0].passed").value(true))
                .andDo(result -> {
                    System.out.println(">>> STATUS: " + result.getResponse().getStatus());
                    System.out.println(">>> BODY: " + result.getResponse().getContentAsString());
                });
    }


    @Test
    void shouldSubmitSuccessfullyWithJwt() throws Exception {
        SubmissionRequest request = new SubmissionRequest();
        request.setProblemId(problemId);
        request.setLanguage("python");
        request.setCode("print(sum(map(int, input().split())))");
        request.setUserId(userRepository.findAll().get(0).getId());
        request.setPvp(false); // в будущем можно протестить и true

        mockMvc.perform(post("/api/code/submit")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passed").value(2))
                .andExpect(jsonPath("$.failed").value(0))
                .andExpect(jsonPath("$.verdict").value("OK"))
                .andDo(result -> {
                    System.out.println(">>> SUBMIT STATUS: " + result.getResponse().getStatus());
                    System.out.println(">>> SUBMIT BODY: " + result.getResponse().getContentAsString());
                });
    }

}

