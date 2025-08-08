package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected TaskStatus status;
    protected String description;
    private LocalDateTime startTime;
    private Duration duration;

    @Override
    public String toString() {
        String str = "model.Task{";
        str += "id='" + this.id + "'";
        str += "title='" + this.name + "'";
        str += ", description='" + this.description + "'";
        str += ", status='" + this.status + "'";
        str += ", startTime='" + this.startTime + "'";
        str += ", duration='" + this.duration + "'";
        str += "}";
        return str;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }


    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (this.getStartTime() != null && this.getDuration() != null) {
            return this.startTime.plus(this.duration);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return Objects.equals(this.id, otherTask.getId());
    }

    public boolean isOverlapped(Task otherTask) {
        LocalDateTime task1Start = this.getStartTime();
        LocalDateTime task1End = this.getEndTime();
        LocalDateTime task2Start = otherTask.getStartTime();
        LocalDateTime task2End = otherTask.getEndTime();

        if (task1Start == null || task1End == null || task2Start == null || task2End == null) {
            return false;
        }
        if (task1Start.isAfter(task2Start) && task1Start.isBefore(task2End)) {
            return true;
        }
        return task2Start.isAfter(task1Start) && task2Start.isBefore(task1End);
    }
}
