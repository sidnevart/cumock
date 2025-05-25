package com.example.cumock.dto.problem;

public class ProblemResponse {

    private Long id;
    private String title;
    private String difficulty;
    private String topic;

    public ProblemResponse(Long id, String title, String difficulty, String topic) {
        this.id = id;

        this.title = title;
        this.difficulty = difficulty;
        this.topic = topic;
    }


    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDifficulty() {
        return difficulty;
    }
    public String getTopic() {
        return topic;
    }
}
