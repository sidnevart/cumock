package com.example.cumock.controller;

import com.example.cumock.dto.problem.CreateProblemRequest;
import com.example.cumock.dto.problem.PaginatedResponse;
import com.example.cumock.dto.problem.ProblemResponse;
import com.example.cumock.dto.problem.UpdateProblemRequest;
import com.example.cumock.model.Problem;
import com.example.cumock.repository.ProblemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/problems")
public class ProblemController {

    private final ProblemRepository problemRepository;

    public ProblemController(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @PostMapping
    public ResponseEntity<Void> createProblem(@RequestBody CreateProblemRequest request) {
        Problem problem = new Problem();
        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setDifficulty(request.getDifficulty());
        problem.setTopic(request.getTopic());
        problemRepository.save(problem);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ProblemResponse>> getAll(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String difficulty
    ) {
        List<Problem> problems;

        if (topic != null && title != null && difficulty != null) {
            problems = problemRepository.findByTopicIgnoreCaseAndTitleContainingIgnoreCaseAndDifficultyIgnoreCase(topic, title, difficulty);
        } else if (topic != null && difficulty != null) {
            problems = problemRepository.findByTopicIgnoreCaseAndDifficultyIgnoreCase(topic, difficulty);
        } else if (title != null && difficulty != null) {
            problems = problemRepository.findByTitleContainingIgnoreCaseAndDifficultyIgnoreCase(title, difficulty);
        } else if (difficulty != null) {
            problems = problemRepository.findByDifficultyIgnoreCase(difficulty);
        } else if (topic != null && title != null) {
            problems = problemRepository.findByTopicIgnoreCaseAndTitleContainingIgnoreCase(topic, title);
        } else if (topic != null) {
            problems = problemRepository.findByTopicIgnoreCase(topic);
        } else if (title != null) {
            problems = problemRepository.findByTitleContainingIgnoreCase(title);
        } else {
            problems = problemRepository.findAll();
        }

        List<ProblemResponse> response = problems.stream()
                .map(p -> new ProblemResponse(p.getId(), p.getTitle(), p.getDifficulty(), p.getTopic()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/paged")
    public ResponseEntity<PaginatedResponse<ProblemResponse>> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<Problem> problemPage = problemRepository.findAll(pageable);

        List<ProblemResponse> problems = problemPage.stream()
                .map(p -> new ProblemResponse(p.getId(), p.getTitle(), p.getDifficulty(), p.getTopic()))
                .toList();

        PaginatedResponse<ProblemResponse> response = new PaginatedResponse<>(
                problems,
                problemPage.getNumber(),
                problemPage.getSize(),
                problemPage.getTotalElements(),
                problemPage.getTotalPages(),
                problemPage.isLast()
        );

        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProblem(@PathVariable Long id, @RequestBody UpdateProblemRequest request) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setDifficulty(request.getDifficulty());
        problem.setTopic(request.getTopic());

        problemRepository.save(problem);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProblem(@PathVariable Long id) {
        if (!problemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        problemRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204
    }


}
