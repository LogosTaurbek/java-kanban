package service;

import history.HistoryManager;
import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private int taskId = 0;
    protected Map<Integer, Task> tasks;
    protected Map<Integer, Subtask> subtasks;
    protected Map<Integer, Epic> epics;
    HistoryManager historyManager;
    TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        taskId = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        Managers managers = new Managers();
        this.historyManager = managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    /* Get */

    private int getCurrentTaskId() {
        taskId++;
        return taskId;
    }

    protected void setTaskId(int newTaskId) {
        this.taskId = newTaskId;
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

    @Override
    public List<Subtask> getSubtasksOfEpic(Epic epic) {
        return epic.getSubtaskIds().stream()
                .map(this::getSubtaskById)
                .collect(Collectors.toList());
    }

    /* End Get */

    /* Create */
    @Override
    public int createTask(Task task) {
        task.setId(this.getCurrentTaskId());
        this.tasks.put(task.getId(), task);
        this.updatePrioritizedTasks();
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
            this.updatePrioritizedTasks();
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
        this.updatePrioritizedTasks();
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
            this.updatePrioritizedTasks();
        }
    }

    protected void updateEpicStatus(Epic epic) {
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

        this.updateEpicDurationInfo(epic);
    }

    protected void updateEpicDurationInfo(Epic epicToUpdate) {
        this.updateEpicStartTime(epicToUpdate);
        this.updateEpicEndTime(epicToUpdate);
        this.updateEpicDuration(epicToUpdate);
    }

    protected void updateEpicStartTime(Epic epicToUpdate) {
        epicToUpdate.setStartTime(
                epicToUpdate.getSubtaskIds().stream()
                        .map(this::getSubtaskById)
                        .map(Task::getStartTime)
                        .filter(Objects::nonNull)
                        .min(LocalDateTime::compareTo)
                        .orElse(null)
        );
    }

    protected void updateEpicEndTime(Epic epicToUpdate) {
        epicToUpdate.setEndTime(
                epicToUpdate.getSubtaskIds().stream()
                        .map(this::getSubtaskById)
                        .map(Task::getEndTime)
                        .filter(Objects::nonNull)
                        .max(LocalDateTime::compareTo)
                        .orElse(null)
        );
    }

    protected void updateEpicDuration(Epic epicToUpdate) {
        if (epicToUpdate.getStartTime() == null || epicToUpdate.getEndTime() == null) {
            epicToUpdate.setDuration(null);
        } else {

            epicToUpdate.setDuration(Duration.between(epicToUpdate.getStartTime(), epicToUpdate.getEndTime()));
        }
    }
    /* End update */

    /* Delete */

    @Override
    public void removeTaskById(int taskId) {
        this.tasks.remove(taskId);
        this.historyManager.remove(taskId);
        this.updatePrioritizedTasks();
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
            this.updatePrioritizedTasks();
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
            this.updatePrioritizedTasks();
        }
    }

    @Override
    public void removeAllTasks() {
        for (Task task : this.getTasks()) {
            this.historyManager.remove(task.getId());
        }
        this.tasks.clear();
        this.updatePrioritizedTasks();

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
        this.updatePrioritizedTasks();
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
        this.updatePrioritizedTasks();
    }

    /* End Delete */
    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(this.prioritizedTasks);
    }

    protected void updatePrioritizedTasks() {
        for (Task task : this.getTasks()) {
            if (task.getStartTime() != null) {
                this.prioritizedTasks.remove(task);
                this.prioritizedTasks.add(task);
            }
        }
        for (Subtask subtask : this.getSubtasks()) {
            if (subtask.getStartTime() != null) {
                this.prioritizedTasks.remove(subtask);
                this.prioritizedTasks.add(subtask);
            }
        }
    }

    private boolean isConflictingTask(Task newTask) {
        boolean hasTaskConflict = this.getTasks().stream()
                .anyMatch(task -> task.isOverlapped(newTask));
        boolean hasSubtaskConflict = this.getSubtasks().stream()
                .anyMatch(subtask -> subtask.isOverlapped(newTask));
        return hasTaskConflict && hasSubtaskConflict;
    }

}

