package com.example.cumock.dto.code_sandbox;

public class CodeExecutionRequest {

    private String code;
    private String language;
    private String input;

    // setters and getters
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getInput() {
        return input;
    }
    public void setInput(String input) {
        this.input = input;
    }
}
