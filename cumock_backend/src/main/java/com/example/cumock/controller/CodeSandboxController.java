package com.example.cumock.controller;

import com.example.cumock.dto.code_sandbox.*;
import com.example.cumock.model.ProblemTestCase;
import com.example.cumock.model.Submission;
import com.example.cumock.repository.ProblemTestCaseRepository;
import com.example.cumock.service.CodeExecutionService;
import com.example.cumock.service.PvPProgressPublisherService;
import com.example.cumock.service.RunResultCacheService;
import com.example.cumock.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/code")
public class CodeSandboxController {

    private final CodeExecutionService executionService;
    private final SubmissionService submissionService;
    private final ProblemTestCaseRepository testCaseRepository;
    private final PvPProgressPublisherService progressPublisherService;
    private final RunResultCacheService runResultCache;

    public CodeSandboxController(CodeExecutionService executionService, ProblemTestCaseRepository testCaseRepository, SubmissionService submissionService, PvPProgressPublisherService progressPublisherService, RunResultCacheService runResultCache) {
        this.executionService = executionService;
        this.testCaseRepository = testCaseRepository;
        this.submissionService = submissionService;
        this.progressPublisherService = progressPublisherService;
        this.runResultCache = runResultCache;

    }

    @PostMapping("/execute")
    public ResponseEntity<CodeExecutionResponse> executeCode(CodeExecutionRequest request) throws IOException, InterruptedException {
        try {
            // Fix parameter order
            CodeResult result = executionService.execute(request.getCode(), request.getInput(), request.getLanguage());
            return ResponseEntity.ok(new CodeExecutionResponse(result));
        } catch(IOException | InterruptedException e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CodeExecutionResponse(
                            new CodeResult("", e.getMessage(), 1, 0L)
                    ));
        }
    }

    @PostMapping("/run")
    public ResponseEntity<RunResult> runCode(@RequestBody CodeRequest request) {

        System.out.println("Running code for problem: " + request.getProblemId());
    
        List<ProblemTestCase> samples = testCaseRepository.findByProblemIdAndIsSampleTrue(request.getProblemId());
        System.out.println("Found " + samples.size() + " test cases");
        
        if (samples.isEmpty()) {
            return ResponseEntity.badRequest().body(new RunResult(List.of(
                new TestResult("", "", "No test cases found for this problem", false, 0L)
            )));
        }
        
        List<TestResult> results = new ArrayList<>();

        for (ProblemTestCase testCase : samples) {
            try {
                // Fix parameter order
                CodeResult exec = executionService.execute(
                        request.getCode(),
                        testCase.getInput(),
                        request.getLanguage()
                );
                boolean passed = exec.getOutput().trim().equals(testCase.getOutput().trim());
                results.add(new TestResult(
                        testCase.getInput(),
                        exec.getOutput(),
                        testCase.getOutput(),
                        passed,
                        exec.getExecutionTimeMillis()
                ));
            } catch (Exception e){
                results.add(new TestResult(
                        testCase.getInput(),
                        "",
                        testCase.getOutput(),
                        false,
                        0L
                ));
            }

        }
        int passed = 0;
        for (TestResult res : results) {
            if (res.isPassed()) passed++;
        }

        if (request.getPvp() != null && request.getPvp()) {
            runResultCache.savePassed(request.getUserId(), request.getProblemId(), request.getContestId(), passed);
            progressPublisherService.publish(request.getContestId(), request.getProblemId(), request.getUserId(), false);
        }
        return ResponseEntity.ok(new RunResult(results));
    }

    @PostMapping("/submit")
    public ResponseEntity<SubmissionResponse> submitCode(@RequestBody SubmissionRequest request) {
        System.out.println("Submitting code for problem: " + request.getProblemId());
        
        List<ProblemTestCase> tests = testCaseRepository.findByProblemId(request.getProblemId());
        System.out.println("Found " + tests.size() + " test cases for submission");
        
        if (tests.isEmpty()) {
            return ResponseEntity.badRequest().body(
                new SubmissionResponse(0, 0, 0, "NO_TEST_CASES", 0)
            );
        }
        if (request.getPvp() != null && request.getPvp()) {
            progressPublisherService.publish(request.getContestId(), request.getProblemId(), request.getUserId(), true);
        }

        int passed = 0;
        int failed = 0;
        long totalTime = 0;

        for (ProblemTestCase test : tests) {
            try {
                CodeResult exec = executionService.execute(request.getCode(), test.getInput(), request.getLanguage());
                totalTime += exec.getExecutionTimeMillis();
                boolean ok = exec.getOutput().trim().equals(test.getOutput().trim());
                if (ok) passed++;
                else failed++;
            } catch (Exception e) {
                failed++;
            }
        }

        String verdict = (passed == tests.size()) ? "OK" : "WRONG_ANSWER";

        int attempt = submissionService.countAttempts(request.getUserId(), request.getProblemId()) + 1;

        Submission submission = new Submission();
        submission.setUserId(request.getUserId());
        submission.setProblemId(request.getProblemId());
        submission.setCode(request.getCode());
        submission.setLanguage(request.getLanguage());
        submission.setPassed(passed);
        submission.setFailed(failed);
        submission.setTotal(tests.size());
        submission.setVerdict(verdict);
        submission.setAttempt(attempt);
        submission.setPvp(request.getPvp() != null && request.getPvp());
        submission.setContestId(request.getContestId());

        submissionService.saveSubmission(submission);

        if (Boolean.TRUE.equals(request.getPvp())) {
            progressPublisherService.publish(request.getContestId(), request.getProblemId(), request.getUserId(), true);
        }

        return ResponseEntity.ok(
                new SubmissionResponse(passed, failed, tests.size(), verdict, totalTime)
        );
    }

}
