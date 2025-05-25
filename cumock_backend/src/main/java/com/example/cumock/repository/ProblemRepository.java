package com.example.cumock.repository;

import com.example.cumock.model.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    Page<Problem> findAll(Pageable pageable);


    List<Problem> findByTopicIgnoreCase(String topic);

    List<Problem> findByTitleContainingIgnoreCase(String title);

    List<Problem> findByTopicIgnoreCaseAndTitleContainingIgnoreCase(String topic, String title);


    List<Problem> findByDifficultyIgnoreCase(String difficulty);

    List<Problem> findByTopicIgnoreCaseAndDifficultyIgnoreCase(String topic, String difficulty);

    List<Problem> findByTitleContainingIgnoreCaseAndDifficultyIgnoreCase(String title, String difficulty);

    List<Problem> findByTopicIgnoreCaseAndTitleContainingIgnoreCaseAndDifficultyIgnoreCase(String topic, String title, String difficulty);



}
