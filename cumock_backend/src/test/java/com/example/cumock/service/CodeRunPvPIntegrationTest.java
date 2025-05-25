package com.example.cumock.service;

import com.example.cumock.dto.code_sandbox.CodeRequest;
import com.example.cumock.model.Problem;
import com.example.cumock.model.ProblemTestCase;
import com.example.cumock.model.Role;
import com.example.cumock.model.User;
import com.example.cumock.repository.ProblemRepository;
import com.example.cumock.repository.ProblemTestCaseRepository;
import com.example.cumock.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CodeRunPvPIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProblemTestCaseRepository testCaseRepository;
    @Autowired private ProblemRepository problemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RunResultCacheService runResultCache;
    @Autowired private JwtService jwtService;


    private Long problemId;
    private Long userId;
    private Long contestId = 123L;
    private String jwt;


    @BeforeEach
    void setup() {
        testCaseRepository.deleteAll();
        problemRepository.deleteAll();
        userRepository.deleteAll();

        var problem = new Problem();
        problem.setTitle("Add");
        problem.setDescription("Add two numbers");
        problem.setDifficulty("easy");
        problem.setTopic("math");
        problemId = problemRepository.save(problem).getId();

        ProblemTestCase t1 = new ProblemTestCase();
        t1.setProblem(problem);
        t1.setInput("1 2");
        t1.setExpectedOutput("3");
        t1.setSample(true);

        ProblemTestCase t2 = new ProblemTestCase();
        t2.setProblem(problem);
        t2.setInput("4 5");
        t2.setExpectedOutput("9");
        t2.setSample(true);

        testCaseRepository.saveAll(List.of(t1, t2));

        var user = new User();
        user.setUsername("tester" + UUID.randomUUID());
        user.setEmail("t@" + UUID.randomUUID() + ".com");
        user.setPasswordHash("123");
        user.setRole(Role.ROLE_USER);
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        userId = userRepository.save(user).getId();
        jwt = jwtService.generateToken(user.getEmail());
    }

    @Test
    void runCodeInPvPShouldStorePassedInRedis() throws Exception {
        CodeRequest request = new CodeRequest();
        request.setUserId(userId);
        request.setProblemId(problemId);
        request.setLanguage("python");
        request.setCode("print(sum(map(int, input().split())))");
        request.setPvp(true);
        request.setContestId(contestId);

        mockMvc.perform(post("/api/code/run")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").isArray());

        int passed = runResultCache.getPassed(userId, problemId, contestId);
        Assertions.assertEquals(2, passed);
    }
}

