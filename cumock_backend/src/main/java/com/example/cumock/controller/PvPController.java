package com.example.cumock.controller;

import com.example.cumock.dto.pvp.PvPChallengeRequest;
import com.example.cumock.dto.pvp.PvPProgressResponse;
import com.example.cumock.model.PvPContest;
import com.example.cumock.model.Submission;
import com.example.cumock.repository.ProblemTestCaseRepository;
import com.example.cumock.repository.PvPContestRepository;
import com.example.cumock.repository.SubmissionRepository;
import com.example.cumock.service.PvPContestService;
import com.example.cumock.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import java.util.Map; 
import java.util.HashMap; 

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/pvp")
public class PvPController {

    private final PvPContestService contestService;
    private final SubmissionRepository submissionRepository;
    private final SubmissionService submissionService;
    private final ProblemTestCaseRepository testCaseRepository;
    private final PvPContestRepository contestRepository;

    public PvPController(
            PvPContestService contestService,
            SubmissionRepository submissionRepository,
            SubmissionService submissionService,
            ProblemTestCaseRepository testCaseRepository,
            PvPContestRepository contestRepository
    ) {
        this.contestService = contestService;
        this.submissionRepository = submissionRepository;
        this.submissionService = submissionService;
        this.testCaseRepository = testCaseRepository;
        this.contestRepository = contestRepository;
    }

    @PostMapping("/challenge")
    public ResponseEntity<PvPContest> createChallenge(@RequestBody PvPChallengeRequest request) {
        try {
            PvPContest contest = contestService.createChallenge(
                    request.getChallengerId(),
                    request.getChallengedId()
            );
            return ResponseEntity.ok(contest);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/challenge/{contestId}/accept")
    public ResponseEntity<PvPContest> acceptChallenge(
            @PathVariable Long contestId,
            @RequestParam Long userId
    ) {
        try {
            PvPContest contest = contestService.acceptChallenge(contestId, userId);
            return ResponseEntity.ok(contest);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/challenge/{contestId}/reject")
    public ResponseEntity<PvPContest> rejectChallenge(
            @PathVariable Long contestId,
            @RequestParam Long userId
    ) {
        try {
            PvPContest contest = contestService.rejectChallenge(contestId, userId);
            return ResponseEntity.ok(contest);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

        @GetMapping("/challenges")
        public ResponseEntity<List<PvPContest>> getChallenges(
                @RequestParam Long userId,
                @RequestParam(defaultValue = "CHALLENGE") String status
        ) {
            try {
                List<PvPContest> user1Challenges = contestRepository.findByUser1IdAndStatus(userId, status);
                List<PvPContest> user2Challenges = contestRepository.findByUser2IdAndStatus(userId, status);
                
                List<PvPContest> allChallenges = new ArrayList<>();
                allChallenges.addAll(user1Challenges);
                allChallenges.addAll(user2Challenges);
                
                System.out.println("Found " + allChallenges.size() + " challenges for user " + userId);
                return ResponseEntity.ok(allChallenges);
            } catch (Exception e) {
                System.err.println("Error fetching challenges: " + e.getMessage());
                return ResponseEntity.status(500).build();
            }
        }

    @GetMapping("/contest/{contestId}")
    public ResponseEntity<PvPContest> getContestDetails(@PathVariable Long contestId) {
        Optional<PvPContest> optional = contestService.getContestById(contestId);
        // получи pvp тесты через repository 
        // List<ProblemTestCase> testCases = testCaseRepository.findPvpTestCasesByProblemId(contestId);


        return optional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/contest/{contestId}/result")
    public ResponseEntity<?> getContestResult(@PathVariable Long contestId) {
        return contestService.getContestById(contestId)
            .map(contest -> {
                Map<String, Object> result = new HashMap<>();
                result.put("contestId", contest.getId());
                result.put("status", contest.getStatus());
                result.put("startTime", contest.getStartTime());
                result.put("endTime", contest.getEndTime());
                result.put("winnerId", contest.getWinnerId());
                
                boolean isFinished = "FINISHED".equals(contest.getStatus());
                result.put("isFinished", isFinished);
                
                if (isFinished && contest.getWinnerId() != null) {
                    boolean isUser1Winner = contest.getWinnerId().equals(contest.getUser1Id());
                    result.put("winner", isUser1Winner ? "user1" : "user2");
                }
                
                return ResponseEntity.ok(result);
            })
            .orElse(ResponseEntity.notFound().build()); 
    }

    @GetMapping("/progress")
    public ResponseEntity<PvPProgressResponse> getProgress(
            @RequestParam Long contestId,
            @RequestParam Long userId,
            @RequestParam Long problemId,
            @RequestParam(defaultValue = "false") boolean isSubmit
    ) {
        Optional<PvPContest> optional = contestService.findOngoingById(contestId);
        if (optional.isEmpty()) return ResponseEntity.notFound().build();

        PvPProgressResponse progress = contestService.getProgress(userId, problemId, contestId, isSubmit);
        return ResponseEntity.ok(progress);
    }
}
