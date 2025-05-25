package com.example.cumock.dto.pvp;

public class PvPProgressResponse {
    private Long userId;
    private Long contestId;
    private int passed;
    private int total;
    private boolean solved;
    private int attempts;


    public PvPProgressResponse(Long userId, Long contestId, int passed, int total, boolean solved, int attempts) {
        this.userId = userId;
        this.contestId = contestId;
        this.passed = passed;
        this.total = total;
        this.solved = solved;
        this.attempts = attempts;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public int getAttempts() {
        return attempts;
    }
    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }
    public boolean isSolved() {
        return solved;
    }
    public void setSolved(boolean solved) {
        this.solved = solved;
    }
    public int getPassed() {
        return passed;
    }
    public void setPassed(int passed) {
        this.passed = passed;
    }
    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public Long getContestId() {
        return contestId;
    }
    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }


}