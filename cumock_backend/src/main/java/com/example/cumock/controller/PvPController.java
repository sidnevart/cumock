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
        List<PvPContest> challenges = contestRepository.findAllByStatus(status).stream()
                .filter(contest -> contest.getUser1Id().equals(userId) || contest.getUser2Id().equals(userId))
                .toList();
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/contest/{contestId}")
    public ResponseEntity<PvPContest> getContestDetails(@PathVariable Long contestId) {
        Optional<PvPContest> optional = contestService.getContestById(contestId);
        return optional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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
