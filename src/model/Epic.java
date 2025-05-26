package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description){
        super(name, description);
        this.subtasksIds = new ArrayList<>();
    }

    public Epic(int id, String name, String description, TaskStatus status, ArrayList<Integer> subtasksIds) {
        super(id, name, description, status);
    }

    @Override
    public String toString() {
        String str = "model.Epic{";
        str += "title='" + this.getName() + "'";
        str += ", description='" + this.getDescription() + "'";
        str += ", status='" + this.getStatus() + "'";
        str += ", subtasksIds='" + this.subtasksIds + "'";
        str += "}";
        return str;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtasksIds;
    }

    public void removeAllSubtasks() {this.subtasksIds.clear();}

    public void addSubtaskId(int subtaskId){
        this.subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskIdToRemove){
        this.subtasksIds.remove((Integer) subtaskIdToRemove);
    }

}
