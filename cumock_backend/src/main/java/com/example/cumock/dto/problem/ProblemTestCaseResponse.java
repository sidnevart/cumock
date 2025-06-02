package com.example.cumock.dto.problem;


public class ProblemTestCaseResponse {
    private Long id;
    private String input;
    private String expectedOutput;
    private boolean isSample;
    private boolean isPvp;

    // конструктор, геттеры
    public ProblemTestCaseResponse(Long id, String input, String expectedOutput, boolean isSample, boolean isPvp) {
        this.id = id;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.isSample = isSample;
        this.isPvp = isPvp;
    }
    public Long getId() {
        return id;
    }
    public String getInput() {
        return input;
    }
    public String getExpectedOutput() {
        return expectedOutput;
    }
    public boolean isSample() {
        return isSample;
    }
    public boolean isPvp() {
        return isPvp;
    }
}