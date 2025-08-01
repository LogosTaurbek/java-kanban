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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager {

    String backUpFilePath;

    public FileBackedTaskManager(String backupFilePath) {
        super();
        this.backUpFilePath = backupFilePath;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
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
        tm.setTaskId(taskId);
        return tm;
    }

    /* Create */
    @Override
    public int createTask(Task task) {
        super.createTask(task);
        this.save();
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
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
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        this.save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        this.save();
    }
    /* End Update */

    /* Remove */
    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        this.save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        this.save();
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        this.save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        this.save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        this.save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        this.save();
    }


    private void addTaskFromFile(Task task) {
        this.tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            this.prioritizedTasks.add(task);
        }
    }

    private void addEpicFromFile(Epic newEpic) {
        this.epics.put(newEpic.getId(), newEpic);
    }

    private void addSubtaskFromFile(Subtask subtask) {
        this.subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) {
            this.prioritizedTasks.add(subtask);
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
    }

    private String taskToCSV(Task task) {
        String id = String.valueOf(task.getId());
        String type = task.getClass().getName();
        String name = task.getName();
        String status = task.getStatus().name();
        String description = task.getDescription();
        String epic = "";
        String startTime = "";
        if (task.getStartTime() != null) {
            startTime = task.getStartTime().toString();
        }
        String duration = "";
        if (task.getDuration() != null) {
            duration = task.getDuration().toString();
        }
        if (type.equals("Subtask")) {
            Subtask st = (Subtask) task;
            epic = String.valueOf(st.getEpicId());
        }

        return String.join(",", id, type, name, status, description, epic, startTime, duration);
    }

    private void save() {
        ArrayList<String> csvLines = new ArrayList<>();
        csvLines.add("id, type, name, status, descriptions, epic, startTime, duration");
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
        String[] values = value.split(",", -1);
        int id = Integer.parseInt(values[0]);
        String type = values[1];
        String name = values[2];
        TaskStatus status = TaskStatus.valueOf(values[3]);
        String description = values[4];
        LocalDateTime startTime = null;
        String startTimeFromFile = values[6];
        if (!Objects.equals(startTimeFromFile, "")) {
            startTime = LocalDateTime.parse(values[6]);
        }
        Duration duration = null;
        String durationFromFile = values[7];
        if (!Objects.equals(durationFromFile, "")) {
            duration = Duration.parse(values[7]);
        }
        Task result = new Task(id, name, description, status);
        switch (type) {
            case "Task" -> result = new Task(id, name, description, status, startTime, duration);
            case "Epic" -> result = new Epic(id, name, description, status, startTime, duration);
            case "Subtask" -> {
                int epicId = Integer.parseInt(values[5]);
                result = new Subtask(id, name, description, status, epicId);
            }
        }

        return result;
    }

}
