package com.example.cumock.dto.pvp;

public class PvPChallengeRequest {
    private Long challengerId;
    private Long challengedId;

    public PvPChallengeRequest() {
    }

    public PvPChallengeRequest(Long challengerId, Long challengedId) {
        this.challengerId = challengerId;
        this.challengedId = challengedId;
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
}