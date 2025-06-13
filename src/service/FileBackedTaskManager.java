package service;

import jdk.jshell.Snippet;
import model.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    String backUpFilePath;
    
    public FileBackedTaskManager (String backupFilePath) {
        super();
        this.backUpFilePath = backupFilePath;
    }

    /* Create */
    @Override
    public int createTask(Task task){
        super.createTask(task);
        this.save();
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic){
        super.createEpic(epic);
        this.save();
        return epic.getId();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        this.save();
        return subtask.getId();

    }
    /* End Create */

    /* Update */
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        this.save();
    }

    @Override
    public void updateEpic(Epic epic){
        super.updateEpic(epic);
        this.save();
    }

    @Override
    public void updateSubtask(Subtask subtask){
        super.updateSubtask(subtask);
        this.save();
    }
    /* End Update */

    /* Remove */
    @Override
    public void removeTaskById(int taskId){
        super.removeTaskById(taskId);
        this.save();
    }

    @Override
    public void removeEpicById(int epicId){
        super.removeEpicById(epicId);
        this.save();
    }

    @Override
    public void removeSubtaskById(int subtaskId){
        super.removeSubtaskById(subtaskId);
        this.save();
    }

    private String taskToCSV (Task task) {
        String id = String.valueOf(task.getId());
        String type = task.getClass().getName();
        String name = task.getName();
        String status = task.getStatus().name();
        String description = task.getDescription();
        String epic = "";
        if(type.equals("Subtask")){
            Subtask st = (Subtask) task;
            epic = String.valueOf(st.getEpicId());
        }
        return String.join(",", id, type, name, status, description, epic);
    }

    private void save () {
        ArrayList<String> csvLines = new ArrayList<>();
        csvLines.add("id, type, name, status, descriptions, epic, startTime,duration");
        for(Task task : this.getTasks()){
            csvLines.add(this.taskToCSV(task));
        }
        for(Epic epic : this.getEpics()){
            csvLines.add(this.taskToCSV(epic));
        }
        for(Subtask subtask : this.getSubtasks()){
            csvLines.add(this.taskToCSV(subtask));
        }
        try (FileWriter writer = new FileWriter(this.backUpFilePath); BufferedWriter br = new BufferedWriter(writer)){
            for(String csvLine : csvLines){
                br.write(csvLine + "\n");
            }
        }
        catch (IOException e) {
            System.out.println("Error");
        }
    }

    private Task fromString(String value){
        String[] values = value.split(",");
        int id = Integer.parseInt(values[0]);
        String type = values[1];
        String name = values[2];
        TaskStatus status = TaskStatus.valueOf(values[3]);
        String description = values[4];

        Task result = new Task(name, description);
        switch (type){
            case "Task" -> result = new Task(id, name, description, status);
            case "Epic" -> result = new Epic(id, name, description, status);
            case "Subtask" -> {
                int epicId = Integer.parseInt(values[3]);
                result = new Subtask(id, name, description, status, epicId);
            }
        }

        return  result;
    }
}
