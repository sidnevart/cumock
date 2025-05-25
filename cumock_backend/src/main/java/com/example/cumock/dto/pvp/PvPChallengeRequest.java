package com.example.cumock.dto.pvp;

public class PvPChallengeRequest {
    private Long challengerId;
    private Long challengedId;
    private Long problem1Id;
    private Long problem2Id;

    public PvPChallengeRequest() {
    }

    public PvPChallengeRequest(Long challengerId, Long challengedId, Long problem1Id, Long problem2Id) {
        this.challengerId = challengerId;
        this.challengedId = challengedId;
        this.problem1Id = problem1Id;
        this.problem2Id = problem2Id;
    }

    public Long getChallengerId() {
        return challengerId;
    }

    public void setChallengerId(Long challengerId) {
        this.challengerId = challengerId;
    }

    public Long getChallengedId() {
        return challengedId;
    }

    public void setChallengedId(Long challengedId) {
        this.challengedId = challengedId;
    }

    public Long getProblem1Id() {
        return problem1Id;
    }

    public void setProblem1Id(Long problem1Id) {
        this.problem1Id = problem1Id;
    }

    public Long getProblem2Id() {
        return problem2Id;
    }

    public void setProblem2Id(Long problem2Id) {
        this.problem2Id = problem2Id;
    }
} 