package com.example.cumock.dto.code_sandbox;

public class CodeExecutionResponse {
    private String output;
    private String error;
    private int exitCode;
    private long executionTimeMillis;

    public CodeExecutionResponse(CodeResult result) {
        this.output = result.getOutput();
        this.error = result.getError();
        this.exitCode = result.getExitCode();
        this.executionTimeMillis = result.getExecutionTimeMillis();
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
    public long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

}
