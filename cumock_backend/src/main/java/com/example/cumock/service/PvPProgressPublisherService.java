package com.example.cumock.service;

import com.example.cumock.dto.pvp.PvPProgressResponse;
import com.example.cumock.model.PvPContest;
import com.example.cumock.model.Submission;
import com.example.cumock.repository.ProblemTestCaseRepository;
import com.example.cumock.repository.PvPContestRepository; // Add this import
import com.example.cumock.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class PvPProgressPublisherService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final RunResultCacheService runResultCache;
    private final ProblemTestCaseRepository testCaseRepository;
    private final SubmissionRepository submissionRepository;
    private final PvPContestRepository contestRepository; // Add this field
    
    @Autowired
    public PvPProgressPublisherService(
            SimpMessagingTemplate messagingTemplate,
            RunResultCacheService runResultCache,
            ProblemTestCaseRepository testCaseRepository,
            SubmissionRepository submissionRepository,
            PvPContestRepository contestRepository) { // Add parameter
        this.messagingTemplate = messagingTemplate;
        this.runResultCache = runResultCache;
        this.testCaseRepository = testCaseRepository;
        this.submissionRepository = submissionRepository;
        this.contestRepository = contestRepository; // Initialize field
    }
    
    public void publish(PvPContest contest) {
        System.out.println("[PvPProgressPublisherService] Publishing progress for contest: " + contest.getId());

        Map<String, Object> user1Progress = getUserProgress(contest.getUser1Id(), contest.getProblem1Id(), contest.getId());
        Map<String, Object> user2Progress = getUserProgress(contest.getUser2Id(), contest.getProblem2Id(), contest.getId());

        System.out.println("[PvPProgressPublisherService] user1Progress: " + user1Progress);
        System.out.println("[PvPProgressPublisherService] user2Progress: " + user2Progress);

        Map<String, Object> payload = new HashMap<>();
        payload.put("contestId", contest.getId());
        payload.put("status", contest.getStatus());
        payload.put("user1Progress", user1Progress);
        payload.put("user2Progress", user2Progress);

        messagingTemplate.convertAndSend("/topic/pvp-progress/" + contest.getId(), payload);
        System.out.println("[PvPProgressPublisherService] Payload sent to /topic/pvp-progress/" + contest.getId());
    }

    public void publish(Long contestId, Long problemId, Long userId, boolean isSubmit) {
        // Find the contest
        PvPContest contest = contestRepository.findById(contestId)
            .orElseThrow(() -> new IllegalArgumentException("Contest not found"));
        
        // Call the existing publish method with the contest object
        publish(contest);
    }
    
    private Map<String, Object> getUserProgress(Long userId, Long problemId, Long contestId) {
        int total = testCaseRepository.countByProblemId(problemId);
        int passed = runResultCache.getPassed(userId, problemId, contestId);
        List<Submission> submissions = submissionRepository.findByUserIdAndProblemIdAndContestId(userId, problemId, contestId);

        boolean solved = submissions.stream().anyMatch(s -> "OK".equals(s.getVerdict()));
        int attempts = submissions.size();

        System.out.println("[PvPProgressPublisherService] User " + userId + " progress: " +
                "passed=" + passed + "/" + total + ", attempts=" + attempts + ", solved=" + solved);

        Map<String, Object> progress = new HashMap<>();
        progress.put("userId", userId);
        progress.put("problemId", problemId);
        progress.put("passed", passed);
        progress.put("total", total);
        progress.put("solved", solved);
        progress.put("attempts", attempts);

        submissions.stream()
                .max(Comparator.comparing(Submission::getCreatedAt))
                .ifPresent(s -> progress.put("lastSubmissionTime", s.getCreatedAt()));

        return progress;
    }


}