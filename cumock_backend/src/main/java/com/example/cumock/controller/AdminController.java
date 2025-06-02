package com.example.cumock.controller;


import com.example.cumock.dto.admin.CreateTestCaseRequest;
import com.example.cumock.dto.problem.CreateProblemRequest;
import com.example.cumock.dto.admin.UpdateUserRoleRequest;
import com.example.cumock.dto.admin.UserAdminResponse;
import com.example.cumock.dto.problem.PaginatedResponse;
import com.example.cumock.dto.problem.ProblemResponse;
import com.example.cumock.dto.problem.UpdateProblemRequest;
import com.example.cumock.dto.problem.ProblemTestCaseResponse;
import com.example.cumock.model.Problem;
import com.example.cumock.model.ProblemTestCase;
import com.example.cumock.model.User;
import com.example.cumock.repository.ProblemRepository;
import com.example.cumock.repository.ProblemTestCaseRepository;
import com.example.cumock.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final ProblemTestCaseRepository testCaseRepository;

    public AdminController(UserRepository userRepository, ProblemRepository problemRepository, ProblemTestCaseRepository testCaseRepository) {
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.testCaseRepository = testCaseRepository;
    }

    /*
    * * * * * * * * * * * * * * * * * * * * * * * *  USERS  * * * * * * * * * * * * * * * * * * * * * * * *
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserAdminResponse>> getAllUsers() {
        List<UserAdminResponse> users = userRepository.findAll().stream()
                .map(u -> new UserAdminResponse(u.getId(), u.getEmail(), u.getUsername(), u.getRole()))
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody UpdateUserRoleRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(request.getRole());
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }


    /*
     * * * * * * * * * * * * * * * * * * * * * * * *  PROBLEMS  * * * * * * * * * * * * * * * * * * * * * * * *
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/problems")
    public ResponseEntity<Void> createProblem(@RequestBody CreateProblemRequest request) {
        Problem problem = new Problem();
        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setDifficulty(request.getDifficulty());
        problem.setTopic(request.getTopic());
        problem.setInputFormat(request.getInputFormat());  // Added
        problem.setOutputFormat(request.getOutputFormat()); // Added
        problem.setExamples(request.getExamples()); 
        problemRepository.save(problem);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/problems")
    public ResponseEntity<List<ProblemResponse>> getAll(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String difficulty
    ) {
        System.out.println(">>> AdminController HIT");

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
    @PutMapping("/problems/{id}")
    public ResponseEntity<?> updateProblem(@PathVariable Long id, @RequestBody UpdateProblemRequest request) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setDifficulty(request.getDifficulty());
        problem.setTopic(request.getTopic());
        problem.setInputFormat(request.getInputFormat());  // Added
        problem.setOutputFormat(request.getOutputFormat()); // Added
        problem.setExamples(request.getExamples()); 
        problemRepository.save(problem);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/problems/{id}")
    public ResponseEntity<?> deleteProblem(@PathVariable Long id) {
        if (!problemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        problemRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204
    }

    @GetMapping("/problems/{problemId}/tests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProblemTestCaseResponse>> getProblemTestCases(@PathVariable Long problemId) {
        List<ProblemTestCase> testCases = testCaseRepository.findByProblemId(problemId);
        List<ProblemTestCaseResponse> response = testCases.stream()
            .map(tc -> new ProblemTestCaseResponse(tc.getId(), tc.getInput(), tc.getExpectedOutput(), tc.isSample(), tc.isPvp()))
            .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/problems/{problemId}/tests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addTestCase(
            @PathVariable Long problemId,
            @RequestBody CreateTestCaseRequest req
    ) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ProblemTestCase testCase = new ProblemTestCase();
        testCase.setProblem(problem);
        testCase.setInput(req.getInput());
        testCase.setExpectedOutput(req.getExpectedOutput());
        testCase.setSample(req.isSample());
        testCase.setPvp(req.isPvp());

        testCaseRepository.save(testCase);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/problems/{problemId}/tests/{testId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTestCase(
            @PathVariable Long problemId,
            @PathVariable Long testId
    ) {
        ProblemTestCase testCase = testCaseRepository.findById(testId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!testCase.getProblem().getId().equals(problemId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        testCaseRepository.delete(testCase);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/problems/{problemId}/tests/{testId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateTestCase(
            @PathVariable Long problemId,
            @PathVariable Long testId,
            @RequestBody CreateTestCaseRequest req
    ) {
        ProblemTestCase testCase = testCaseRepository.findById(testId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!testCase.getProblem().getId().equals(problemId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        testCase.setInput(req.getInput());
        testCase.setExpectedOutput(req.getExpectedOutput());
        testCase.setSample(req.isSample());
        testCase.setPvp(req.isPvp());

        testCaseRepository.save(testCase);
        return ResponseEntity.ok().build();
    }


}
