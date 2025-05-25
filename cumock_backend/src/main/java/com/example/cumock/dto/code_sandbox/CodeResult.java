package com.example.cumock.dto.code_sandbox;

public class CodeResult {
    private String output;
    private String error;
    private int exitCode;
    private Long executionTimeMillis;

    public CodeResult(String output, String error, int exitCode, Long executionTimeMillis) {
        this.output = output;
        this.error = error;
        this.exitCode = exitCode;
        this.executionTimeMillis = executionTimeMillis;
    }

    public String getOutput() {
        return output;
    }
    public String getError() {
        return error;
    }
    public int getExitCode() {
        return exitCode;
    }
    public Long getExecutionTimeMillis() {
        return executionTimeMillis;
    }


}
