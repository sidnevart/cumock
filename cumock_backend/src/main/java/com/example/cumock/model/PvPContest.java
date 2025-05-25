package com.example.cumock.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "pvp_contest")
public class PvPContest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user1_id", nullable = false)
    private Long user1Id;

    @Column(name = "user2_id", nullable = false)
    private Long user2Id;

    @Column(name = "problem1_id", nullable = false)
    private Long task1Id;

    @Column(name = "problem2_id", nullable = false)
    private Long task2Id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status; // CHALLENGE, WAITING, ONGOING, FINISHED, REJECTED

    @Column(name = "challenge_expires_at")
    private LocalDateTime challengeExpiresAt;

    @Column(name = "winner_id")
    private Long winnerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "WAITING";
        }
    }


    // getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUser1Id() {
        return user1Id;
    }
    public void setUser1Id(Long user1Id) {
        this.user1Id = user1Id;
    }
    public Long getUser2Id() {
        return user2Id;
    }
    public void setUser2Id(Long user2Id) {
        this.user2Id = user2Id;
    }
    public Long getProblem1Id() {
        return task1Id;
    }
    public void setProblem1Id(Long task1Id) {
        this.task1Id = task1Id;
    }

    public Long getProblem2Id() {
        return task2Id;
    }
    public void setProblem2Id(Long task2Id) {
        this.task2Id = task2Id;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Long getWinnerId() {
        return winnerId;
    }
    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getChallengeExpiresAt() {
        return challengeExpiresAt;
    }
    public void setChallengeExpiresAt(LocalDateTime challengeExpiresAt) {
        this.challengeExpiresAt = challengeExpiresAt;
    }

}
