package com.example.cumock.model;

public class ProblemExample {
    private String input;
    private String output;
    
    // No-arg constructor required for Jackson
    public ProblemExample() {
    }
    
    public ProblemExample(String input, String output) {
        this.input = input;
        this.output = output;
    }
    
    public String getInput() {
        return input;
    }
    
    public void setInput(String input) {
        this.input = input;
    }
    
    public String getOutput() {
        return output;
    }
    
    public void setOutput(String output) {
        this.output = output;
    }
}