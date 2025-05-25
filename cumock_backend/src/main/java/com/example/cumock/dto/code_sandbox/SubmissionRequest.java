package com.example.cumock.dto.code_sandbox;

import lombok.Data;


public class SubmissionRequest {
    private Long userId;
    private Long problemId;
    private String code;
    private String language;
    private Boolean pvp = false;
    private Long contestId;

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getProblemId() {
        return problemId;
    }
    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }
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
    public Boolean getPvp() {
        return pvp;
    }
    public void setPvp(Boolean pvp) {
        this.pvp = pvp;
    }
    public Long getContestId() {
        return contestId;
    }
    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

}

