package com.example.cumock.dto.code_sandbox;

import lombok.AllArgsConstructor;
import lombok.Data;

public class SubmissionResponse {
    private int passed;
    private int failed;
    private int total;
    private String verdict;
    private long executionTimeMillis;

    public SubmissionResponse(int passed, int failed, int total, String verdict, long executionTimeMillis) {
        this.passed = passed;
        this.failed = failed;
        this.total = total;
        this.verdict = verdict;
        this.executionTimeMillis = executionTimeMillis;
    }

    // getters and setters
    public int getPassed() {
        return passed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    public void setExecutionTimeMillis(long executionTimeMillis) {
        this.executionTimeMillis = executionTimeMillis;
    }

}
