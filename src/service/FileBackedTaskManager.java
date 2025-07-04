package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    String backUpFilePath;

    public FileBackedTaskManager(String backupFilePath) {
        super();
        this.backUpFilePath = backupFilePath;
    }

    public FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        String rawBackUp = "";
        try {
            rawBackUp = Files.readString(file.toPath());
        } catch (IOException e) {
            //Exception when reading file
            throw new ManagerSaveException("Error in reading from file " + file.getName());
        }
        FileBackedTaskManager tm = new FileBackedTaskManager(file.getPath());

        String[] taskStrs = rawBackUp.split("\n");
        int taskId = 0;

        for (int i = 1; i < taskStrs.length; i++) {
            Task taskFromStr = tm.fromString(taskStrs[i]);
            if (taskFromStr instanceof Epic) {
                tm.addEpicFromFile((Epic) taskFromStr);
            } else if (taskFromStr instanceof Subtask) {
                tm.addSubtaskFromFile((Subtask) taskFromStr);
            } else {
                tm.addTaskFromFile(taskFromStr);
            }
            if (taskFromStr.getId() > taskId) {
                taskId = taskFromStr.getId();
            }

        }
        setTaskId(taskId + 1);
        return tm;
    }

    /* Create */
    @Override
    public int createTask(Task task) throws ManagerSaveException {
        super.createTask(task);
        this.save();
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) throws ManagerSaveException {
        super.createEpic(epic);
        this.save();
        return epic.getId();
    }

    @Override
    public int createSubtask(Subtask subtask) throws ManagerSaveException {
        super.createSubtask(subtask);
        this.save();
        return subtask.getId();

    }
    /* End Create */

    /* Update */
    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        super.updateTask(task);
        this.save();
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        super.updateEpic(epic);
        this.save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        super.updateSubtask(subtask);
        this.save();
    }
    /* End Update */

    /* Remove */
    @Override
    public void removeTaskById(int taskId) throws ManagerSaveException {
        super.removeTaskById(taskId);
        this.save();
    }

    @Override
    public void removeEpicById(int epicId) throws ManagerSaveException {
        super.removeEpicById(epicId);
        this.save();
    }

    @Override
    public void removeSubtaskById(int subtaskId) throws ManagerSaveException {
        super.removeSubtaskById(subtaskId);
        this.save();
    }

    @Override
    public void removeAllTasks() throws ManagerSaveException {
        super.removeAllTasks();
        this.save();
    }

    @Override
    public void removeAllEpics() throws ManagerSaveException {
        super.removeAllEpics();
        this.save();
    }

    @Override
    public void removeAllSubtasks() throws ManagerSaveException {
        super.removeAllSubtasks();
        this.save();
    }


    private void addTaskFromFile(Task task) {
        this.tasks.put(task.getId(), task);
    }

    private void addEpicFromFile(Epic newEpic) {
        this.epics.put(newEpic.getId(), newEpic);
    }

    private void addSubtaskFromFile(Subtask subtask) {
        this.subtasks.put(subtask.getId(), subtask);
        for (Epic epic : this.getEpics()) {
            if (epic.getId() == subtask.getEpicId()) {
                epic.addSubtaskId(subtask.getId());
            }
        }


    }

    private String taskToCSV(Task task) {
        String id = String.valueOf(task.getId());
        String type = task.getClass().getName();
        String name = task.getName();
        String status = task.getStatus().name();
        String description = task.getDescription();
        String epic = "";
        if (type.equals("Subtask")) {
            Subtask st = (Subtask) task;
            epic = String.valueOf(st.getEpicId());
        }

        return String.join(",", id, type, name, status, description, epic);
    }

    private void save() throws ManagerSaveException {
        ArrayList<String> csvLines = new ArrayList<>();
        csvLines.add("id, type, name, status, descriptions, epic");
        for (Task task : this.getTasks()) {
            csvLines.add(this.taskToCSV(task));
        }
        for (Epic epic : this.getEpics()) {
            csvLines.add(this.taskToCSV(epic));
        }
        for (Subtask subtask : this.getSubtasks()) {
            csvLines.add(this.taskToCSV(subtask));
        }
        try (BufferedWriter br = new BufferedWriter(new FileWriter(this.backUpFilePath))) {
            for (String csvLine : csvLines) {
                br.write(csvLine + "\n");
            }
        } catch (IOException e) {
            //Exception in BufferedWriter
            throw new ManagerSaveException("Error in BufferedWriter");
        }
    }

    private Task fromString(String value) {
        String[] values = value.split(",");
        int id = Integer.parseInt(values[0]);
        String type = values[1];
        String name = values[2];
        TaskStatus status = TaskStatus.valueOf(values[3]);
        String description = values[4];

        Task result = new Task(id, name, description, status);
        switch (type) {
            case "Task" -> result = new Task(id, name, description, status);
            case "Epic" -> result = new Epic(id, name, description, status);
            case "Subtask" -> {
                int epicId = Integer.parseInt(values[3]);
                result = new Subtask(id, name, description, status, epicId);
            }
        }

        return result;
    }

}
