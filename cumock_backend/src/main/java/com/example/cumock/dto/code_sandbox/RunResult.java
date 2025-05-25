package com.example.cumock.dto.code_sandbox;

import java.util.List;

public class RunResult {
    private List<TestResult> results;

    public RunResult(List<TestResult> results) {
        this.results = results;
    }

    public List<TestResult> getResults() {
        return results;
    }
}
