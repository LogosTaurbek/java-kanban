package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksIds = new ArrayList<>();
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    public Epic(int id, String name, String description, TaskStatus status, ArrayList<Integer> subtasksIds) {
        super(id, name, description, status);
    }

    public Epic(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.subtasksIds = new ArrayList<>();
    }

    public Epic(int id, String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
    }

    public Epic(int id, String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration, ArrayList<Integer> subtasksIds) {
        super(id, name, description, status, startTime, duration);
    }

    @Override
    public String toString() {
        String str = "model.Epic{";
        str += "title='" + this.getName() + "'";
        str += ", description='" + this.getDescription() + "'";
        str += ", status='" + this.getStatus() + "'";
        str += ", subtasksIds='" + this.subtasksIds + "'";
        str += ", startTime='" + this.getStartTime() + "'";
        str += ", duration='" + this.getDuration() + "'";
        str += "}";
        return str;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtasksIds;
    }

    public void removeAllSubtasks() {
        this.subtasksIds.clear();
    }

    public void addSubtaskId(Integer subtaskId) {
        this.subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskIdToRemove) {
        this.subtasksIds.remove((Integer) subtaskIdToRemove);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

}
