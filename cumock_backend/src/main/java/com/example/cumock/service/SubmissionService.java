package com.example.cumock.service;

import com.example.cumock.model.Submission;
import com.example.cumock.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    public Submission saveSubmission(Submission submission) {
        submission.setCreatedAt(LocalDateTime.now());
        return submissionRepository.save(submission);
    }

    public List<Submission> getSubmissionsByUserAndProblem(Long userId, Long problemId) {
        return submissionRepository.findByUserIdAndProblemId(userId, problemId);
    }

    public int countAttempts(Long userId, Long problemId) {
        return submissionRepository.countByUserIdAndProblemId(userId, problemId);
    }
}
