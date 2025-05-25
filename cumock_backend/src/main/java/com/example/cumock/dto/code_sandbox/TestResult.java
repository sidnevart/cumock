package com.example.cumock.dto.code_sandbox;

public class TestResult {
    private String input;
    private String output;
    private String expected;
    private boolean passed;
    private long executionTimeMillis;

    public TestResult(String input, String output, String expected, boolean passed, long executionTimeMillis) {
        this.input = input;
        this.output = output;
        this.expected = expected;
        this.passed = passed;
        this.executionTimeMillis = executionTimeMillis;
    }

    public String getInput() {
        return input;
    }
    public String getOutput() {
        return output;
    }
    public String getExpected() {
        return expected;
    }
    public boolean isPassed() {
        return passed;
    }
    public long getExecutionTimeMillis() {
        return executionTimeMillis;
    }
}

