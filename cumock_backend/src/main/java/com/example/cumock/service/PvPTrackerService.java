package com.example.cumock.service;

import com.example.cumock.dto.pvp.PvPProgressResponse;
import com.example.cumock.model.ProblemTestCase;
import com.example.cumock.model.Submission;
import com.example.cumock.repository.ProblemTestCaseRepository;
import com.example.cumock.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PvPTrackerService {

    private final SubmissionRepository submissionRepository;
    private final ProblemTestCaseRepository testCaseRepository;
    private final RunResultCacheService runCache;

    // constructor injection
    public PvPTrackerService(SubmissionRepository submissionRepository, ProblemTestCaseRepository testCaseRepository, RunResultCacheService runCache) {
        this.submissionRepository = submissionRepository;
        this.testCaseRepository = testCaseRepository;
        this.runCache = runCache;
    }

    public PvPProgressResponse calculateProgress(Long userId, Long problemId, Long contestId, boolean isSubmit) {
        int total = testCaseRepository.findByProblemId(problemId).size();
        int passed = 0;
        int attempts = 0;
        boolean solved = false;

        if (isSubmit) {
            List<Submission> submissions = submissionRepository
                    .findByUserIdAndProblemIdAndContestId(userId, problemId, contestId);

            attempts = submissions.size();
            solved = submissions.stream().anyMatch(s -> "OK".equals(s.getVerdict()));
            passed = solved ? total : 0;

        } else {
            passed = runCache.getPassed(userId, problemId, contestId);
        }

        return new PvPProgressResponse(userId, contestId, passed, total, solved, attempts);
    }
}
