package com.example.cumock.dto.code_sandbox;


public class CodeRequest {
    private Long problemId;
    private String code;
    private String language;

    // PvP support
    private Boolean pvp = false;
    private Long contestId;
    private Long userId;

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
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

}

