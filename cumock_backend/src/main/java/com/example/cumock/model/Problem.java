package com.example.cumock.model;


import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import com.example.cumock.util.JsonConverter;
import com.example.cumock.model.ProblemExample;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;

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

    @Column(name = "input_format", columnDefinition = "TEXT")
    private String inputFormat;
    
    @Column(name = "output_format", columnDefinition = "TEXT")
    private String outputFormat;

    @Column(name = "examples", columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    private List<ProblemExample> examples = new ArrayList<>();


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


    public String getInputFormat() {
        return inputFormat;
    }
    
    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }
    
    public String getOutputFormat() {
        return outputFormat;
    }
    
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
    
    public List<ProblemExample> getExamples() {
        return examples;
    }
    
    public void setExamples(List<ProblemExample> examples) {
        this.examples = examples;
    }
}
