package com.example.cumock.model;


import jakarta.persistence.*;

@Entity
@Table(name = "problems")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String description;

    private String difficulty; // "easy", "medium", "hard"

    private String topic;

    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getDifficulty() {
        return difficulty;
    }
    public String getTopic() {
        return topic;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }
}
