package com.example.cumock.controller;

import com.example.cumock.model.*;
import com.example.cumock.repository.ProblemRepository;
import com.example.cumock.repository.ProblemTestCaseRepository;
import com.example.cumock.repository.PvPContestRepository;
import com.example.cumock.repository.UserRepository;
import com.example.cumock.service.JwtService;
import com.example.cumock.service.RunResultCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PvPProgressApiTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProblemTestCaseRepository testCaseRepository;
    @Autowired private ProblemRepository problemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RunResultCacheService runResultCache;
    @Autowired private PvPContestRepository contestRepository;
    @Autowired private JwtService jwtService;


    private Long problemId;
    private Long userId;
    private Long contestId;
    private String jwt;

    @BeforeEach
    void setup() {
        testCaseRepository.deleteAll();
        problemRepository.deleteAll();
        userRepository.deleteAll();
        contestRepository.deleteAll();

        Problem problem = new Problem();
        problem.setTitle("PvP Test");
        problem.setDescription("Add numbers");
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

        User user = new User();
        user.setUsername("user" + UUID.randomUUID());
        user.setEmail("test@" + UUID.randomUUID() + ".com");
        user.setPasswordHash("123");
        user.setRole(Role.ROLE_USER);
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        userId = userRepository.save(user).getId();
        jwt = jwtService.generateToken(user.getEmail());


        PvPContest contest = new PvPContest();
        contest.setUser1Id(userId);
        contest.setUser2Id(userId);
        contest.setProblem1Id(problemId);
        contest.setProblem2Id(problemId);
        contest.setStartTime(LocalDateTime.now());
        contest.setStatus("ONGOING");

        contestId = contestRepository.save(contest).getId();
        System.out.println(">>> Сохранили статус: [" + contest.getStatus() + "]");



        runResultCache.savePassed(userId, problemId, contestId, 2);
    }

    @Test
    void getProgress_shouldReturnCorrectData() throws Exception {
        mockMvc.perform(get("/api/pvp/progress")
                        .header("Authorization", "Bearer " + jwt)
                        .param("contestId", contestId.toString())
                        .param("userId", userId.toString())
                        .param("problemId", problemId.toString())
                        .param("isSubmit", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.contestId").value(contestId))
                .andExpect(jsonPath("$.passed").value(2))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.solved").value(false))
                .andExpect(jsonPath("$.attempts").value(0))
                .andDo(result -> {
                    System.out.println(">>> STATUS: " + result.getResponse().getStatus());
                    System.out.println(">>> BODY: " + result.getResponse().getContentAsString());
                });

    }
}

