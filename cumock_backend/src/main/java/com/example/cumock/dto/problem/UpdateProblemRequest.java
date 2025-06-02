package com.example.cumock.dto.problem;
import java.util.List;
import com.example.cumock.model.ProblemExample;

public class UpdateProblemRequest {
    private String title;
    private String description;
    private String difficulty;
    private String topic;
    private String inputFormat;
    private String outputFormat;
    private List<ProblemExample> examples;

    // getters and setters
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    public String getTopic() {
        return topic;
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
