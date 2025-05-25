package com.example.cumock.repository;

import com.example.cumock.model.ProblemTestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemTestCaseRepository extends JpaRepository<ProblemTestCase, Long> {
    List<ProblemTestCase> findByProblemIdAndIsSampleTrue(Long problemId);

    List<ProblemTestCase> findByProblemId(Long problemId);
}
