package com.example.cumock.repository;

import com.example.cumock.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByUserIdAndProblemId(Long userId, Long problemId);

    int countByUserIdAndProblemId(Long userId, Long problemId);

    List<Submission> findByContestIdAndVerdict(Long contestId, String ok);

    List<Submission> findByUserIdAndContestId(Long opponentId, Long contestId);

    int countByUserIdAndProblemIdAndContestId(Long userId, Long problemId, Long contestId);

    List<Submission> findByUserIdAndProblemIdAndContestId(Long userId, Long problemId, Long contestId);
    // findByUserIdAndVerdict(Long,String)
    List<Submission> findByUserIdAndVerdict(Long userId, String verdict);
}