package service;

import history.HistoryManager;
import model.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private int taskId = 0;
    private Map<Integer, Task> tasks;
    private Map<Integer, Subtask> subtasks;
    private Map<Integer, Epic> epics;
    HistoryManager historyManager;

    public InMemoryTaskManager() {
        taskId = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        Managers managers = new Managers();
        this.historyManager = managers.getDefaultHistory();
    }

    /* Get */

    private int getCurrentTaskId() {
        taskId++;
        return taskId;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(this.epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(this.subtasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = this.tasks.get(id);
        this.historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = this.epics.get(id);
        this.historyManager.add(epic);
        return epic;
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = this.epics.get(epicId);
        if (epic != null) {
            ArrayList<Subtask> subtasks = new ArrayList<>();
            ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
            for (Integer subtaskId : subtaskIds) {
                subtasks.add(this.subtasks.get(subtaskId));
            }
            return subtasks;
        } else {
            return null;
        }
    }

    public Subtask getSubtaskById(int id) {
        Subtask subtask = this.subtasks.get(id);
        this.historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return this.historyManager.getHistory();
    }

    /* End Get */

    /* Create */
    @Override
    public int createTask(Task task) {
        task.setId(this.getCurrentTaskId());
        this.tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(this.getCurrentTaskId());
        this.epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int createSubtask(Subtask subtask) {

        Epic connectedEpic = this.epics.get(subtask.getEpicId());
        if (connectedEpic != null) {
            subtask.setId(this.getCurrentTaskId());
            this.subtasks.put(subtask.getId(), subtask);
            connectedEpic.addSubtaskId(subtask.getId());
            this.updateEpicStatus(connectedEpic);
        } else {
            return -1;
        }
        return subtask.getId();
    }

    /* End create */

    /* Update */

    @Override
    public void updateTask(Task task) {
        if (this.tasks.get(task.getId()) == null) {
            return;
        }
        this.tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic epicToUpdate = this.epics.get(epic.getId());
        if (epicToUpdate != null) {
            epicToUpdate.setName(epic.getName());
            epicToUpdate.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask subtaskToUpdate = this.subtasks.get(subtask.getId());
        if (subtaskToUpdate == null) {
            return;
        }
        Subtask updatedSubtask = this.subtasks.get(subtask.getId());
        if (updatedSubtask.getEpicId() == subtask.getEpicId()) {
            updatedSubtask.setName(subtask.getName());
            updatedSubtask.setDescription(subtask.getDescription());
            updatedSubtask.setStatus(subtask.getStatus());
            Epic epicToUpdate = this.epics.get(subtask.getEpicId());
            this.updateEpicStatus(epicToUpdate);
        }
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean isNew = false;
        boolean isInProgress = false;
        boolean isDone = false;

        ArrayList<Integer> epicSubtasksIds = epic.getSubtaskIds();

        for (int i = 0; i < epicSubtasksIds.size(); i++) {
            Subtask curSubtask = this.subtasks.get(epicSubtasksIds.get(i));
            TaskStatus status = curSubtask.getStatus();
            if (status == TaskStatus.NEW) {
                isNew = true;
            } else if (status == TaskStatus.IN_PROGRESS) {
                isInProgress = true;
            } else if (status == TaskStatus.DONE) {
                isDone = true;
            }
        }
        if (isNew && !isInProgress && !isDone) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isDone && !isInProgress && !isNew) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    /* End update */

    /* Delete */

    @Override
    public void removeTaskById(int taskId) {
        this.tasks.remove(taskId);
        this.historyManager.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        Epic epic = this.epics.get(epicId);
        if (epic != null) {
            ArrayList<Integer> subtaskIds = new ArrayList<>(epic.getSubtaskIds());
            for (int subtaskId : subtaskIds) {
                this.subtasks.remove(subtaskId);
                this.historyManager.remove(subtaskId);
            }
            this.epics.remove(epicId);
            this.historyManager.remove(epicId);
        }
    }

    @Override
    public void removeSubtaskById(int idToRemove) {
        Subtask subtaskToRemove = this.subtasks.get(idToRemove);
        if (subtaskToRemove != null) {
            Epic epic = this.epics.get(subtaskToRemove.getEpicId());
            epic.removeSubtaskId(idToRemove);
            this.updateEpicStatus(epic);
            this.subtasks.remove(idToRemove);
            this.historyManager.remove(idToRemove);
        }
    }

    @Override
    public void removeAllTasks() {
        for (Task task : this.getTasks()) {
            this.historyManager.remove(task.getId());
        }
        this.tasks.clear();

    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : this.getEpics()) {
            this.historyManager.remove(epic.getId());
        }
        for (Subtask subtask : this.getSubtasks()) {
            this.historyManager.remove(subtask.getId());
        }
        this.epics.clear();
        this.subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        HashSet<Epic> epics = new HashSet<>();
        for (Subtask subtask : this.getSubtasks()) {
            Epic connectedEpic = this.epics.get(subtask.getEpicId());
            epics.add(connectedEpic);
            int subtaskId = subtask.getId();
            connectedEpic.removeSubtaskId(subtaskId);
            this.historyManager.remove(subtaskId);
        }
        for (Epic epic : this.getEpics()) {
            epic.removeAllSubtasks();
            this.updateEpicStatus(epic);
        }
        this.subtasks.clear();
    }

    /* End Delete */

}

