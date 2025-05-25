package com.example.cumock.dto.admin;

public class CreateTestCaseRequest {
    private String input;
    private String expectedOutput;
    private boolean sample; // 🔥 вместо isSample
    private boolean pvp;

    public String getInput() {
        return input;
    }
    public void setInput(String input) {
        this.input = input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }
    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public boolean isSample() {
        return sample;
    }
    public void setSample(boolean sample) {
        this.sample = sample;
    }

    public boolean isPvp() {
        return pvp;
    }
    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }
}


