package com.example.cumock.repository;

import com.example.cumock.model.ProblemTestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProblemTestCaseRepository extends JpaRepository<ProblemTestCase, Long> {
    List<ProblemTestCase> findByProblemIdAndIsSampleTrue(Long problemId);

    List<ProblemTestCase> findByProblemId(Long problemId);

    // pvp tests, not sample
    @Query("SELECT t FROM ProblemTestCase t WHERE t.problem.id = :problemId AND t.isPvp = false")
    List<ProblemTestCase> findPvpTestCasesByProblemId(@Param("problemId") Long problemId);

    @Query("SELECT COUNT(t) FROM ProblemTestCase t WHERE t.problem.id = :problemId")
    int countByProblemId(@Param("problemId") Long problemId);

    List<ProblemTestCase> findByProblemIdAndIsPvpTrue(Long problemId);

}
