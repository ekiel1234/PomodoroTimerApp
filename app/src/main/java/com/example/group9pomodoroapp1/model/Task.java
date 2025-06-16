package com.example.group9pomodoroapp1.model;

public class Task {
    private String name;
    private int estimatedPomodoros;
    private int completedPomodoros;
    private int distractionCount;

    private long completedAt; // timestamp

    public Task(String name, int estimatedPomodoros) {
        this.name = name;
        this.estimatedPomodoros = estimatedPomodoros;
        this.completedPomodoros = 0;
        this.distractionCount = 0;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getEstimatedPomodoros() {
        return estimatedPomodoros;
    }

    public int getCompletedPomodoros() {
        return completedPomodoros;
    }

    public int getDistractionCount() {
        return distractionCount;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEstimatedPomodoros(int estimatedPomodoros) {
        this.estimatedPomodoros = estimatedPomodoros;
    }

    public void setCompletedPomodoros(int completedPomodoros) {
        this.completedPomodoros = completedPomodoros;
    }

    public void setDistractionCount(int distractionCount) {
        this.distractionCount = distractionCount;
    }

    // Increment methods
    public void incrementCompletedPomodoros() {
        this.completedPomodoros++;
    }

    public void incrementDistractionCount() {
        this.distractionCount++;
    }

    // Aliases for MainActivity compatibility
    public void incrementProgress() {
        incrementCompletedPomodoros();
    }

    public int getProgress() {
        return getCompletedPomodoros();
    }
}
