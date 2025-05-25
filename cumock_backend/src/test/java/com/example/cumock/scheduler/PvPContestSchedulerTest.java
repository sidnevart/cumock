package com.example.cumock.scheduler;

import com.example.cumock.model.*;
import com.example.cumock.repository.*;
import com.example.cumock.service.PvPContestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles("test")
class PvPContestSchedulerTest {

    @Autowired
    private PvPContestRepository contestRepository;
    @Autowired private SubmissionRepository submissionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProblemRepository problemRepository;
    @Autowired private ProblemTestCaseRepository testCaseRepository;
    @Autowired private PvPContestService pvpContestService;

    private Long problemId;
    private Long user1Id;
    private Long user2Id;
    private Long contestId;

    @BeforeEach
    void setup() {
        contestRepository.deleteAll();
        submissionRepository.deleteAll();
        testCaseRepository.deleteAll();
        problemRepository.deleteAll();
        userRepository.deleteAll();

        Problem problem = new Problem();
        problem.setTitle("PvP Timer");
        problem.setDescription("Test");
        problem.setDifficulty("easy");
        problem.setTopic("math");
        problemId = problemRepository.save(problem).getId();

        ProblemTestCase test = new ProblemTestCase();
        test.setProblem(problem);
        test.setInput("3 4");
        test.setExpectedOutput("7");
        test.setSample(false);
        testCaseRepository.save(test);

        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("u1@test.com");
        user1.setPasswordHash("123");
        user1.setRole(Role.ROLE_USER);
        user1.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        user1Id = userRepository.save(user1).getId();

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("u2@test.com");
        user2.setPasswordHash("123");
        user2.setRole(Role.ROLE_USER);
        user2.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        user2Id = userRepository.save(user2).getId();

        PvPContest contest = new PvPContest();
        contest.setUser1Id(user1Id);
        contest.setUser2Id(user2Id);
        contest.setProblem1Id(problemId);
        contest.setProblem2Id(problemId);
        contest.setStartTime(LocalDateTime.now().minusMinutes(31)); // имитация истекшего времени
        contest.setStatus("ONGOING");
        contestId = contestRepository.save(contest).getId();

        // Побеждает user1
        Submission success = new Submission();
        success.setUserId(user1Id);
        success.setProblemId(problemId);
        success.setContestId(contestId);
        success.setPvp(true);
        success.setAttempt(1);
        success.setCode("...");
        success.setLanguage("python");
        success.setVerdict("OK");
        success.setPassed(1);
        success.setFailed(0);
        success.setTotal(1);
        success.setCreatedAt(LocalDateTime.now());

        submissionRepository.save(success);
    }

    @Test
    void schedulerShouldEndContestAndPickWinner() {
        pvpContestService.endMatchWithEvaluation(contestId);

        PvPContest updated = contestRepository.findById(contestId).orElseThrow();
        Assertions.assertEquals("FINISHED", updated.getStatus());
        Assertions.assertEquals(user1Id, updated.getWinnerId());
    }
}

