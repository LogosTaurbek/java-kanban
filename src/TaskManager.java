import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TaskManager {

    int taskId = 0;
    HashMap<Integer, Task> tasks;
    HashMap<Integer, Subtask> subtasks;
    HashMap<Integer, Epic> epics;

    public TaskManager(){
        taskId = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    int getCurrentTaskId () {
        taskId++;
        return taskId;
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(this.epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(this.subtasks.values());
    }

    /* Create */

    public int createTask (Task task) {
        task.setId(this.getCurrentTaskId());
        this.tasks.put(task.getId(), task);
        return task.getId();
    }

    public int createEpic(Epic epic) {
        epic.setId(this.getCurrentTaskId());
        this.epics.put(epic.getId(), epic);
        return epic.getId();
    }
    public int createSubtask(Subtask subtask) {
        subtask.setId(this.getCurrentTaskId());
        this.subtasks.put(subtask.getId(), subtask);
        Epic connectedEpic = getEpicById(subtask.getEpicId());
        connectedEpic.addSubtaskId(subtask.getId());
        this.updateEpicStatus(connectedEpic);
        return subtask.getId();
    }

    /* End create */

    /* Update */

    public void updateTask(Task task) {
        this.tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        this.epics.put(epic.getId(), epic);
        this.updateEpicStatus(epic);
    }

    public void updateSubtask(Subtask subtask) {
        this.subtasks.put(subtask.getId(), subtask);
        Epic epicToUpdate = this.getEpicById(subtask.getEpicId());
        this.updateEpicStatus(epicToUpdate);
    }

    /* End update */

    /* Delete */

    public void removeTaskById(int taskId) {
        this.tasks.remove(taskId);
    }

    public void removeEpicById(int epicId) {
        Epic epic = this.getEpicById(epicId);
        if (epic != null) {
            ArrayList<Integer> subtaskIds = new ArrayList<>(epic.getSubtaskIds());
            for (int subtaskId : subtaskIds) {
                this.removeSubtaskById(subtaskId);
            }
            this.epics.remove(epicId);
        }
    }

    public void removeSubtaskById(int idToRemove) {
        Subtask subtaskToRemove = this.getSubtaskById(idToRemove);
        Epic epic = this.getEpicById(subtaskToRemove.getEpicId());
        epic.removeSubtaskId(idToRemove);
        this.updateEpicStatus(epic);
        this.subtasks.remove(idToRemove);
    }

    public void removeAllSubtasks() {
        HashSet<Epic> epicsToUpdate = new HashSet<>();
        for (Subtask subtask : this.getSubtasks()) {
            Epic connectedEpic = this.getEpicById(subtask.getEpicId());
            epicsToUpdate.add(connectedEpic);
            connectedEpic.removeSubtaskId(subtask.getId());
        }
        for (Epic epic : epicsToUpdate) {
            this.updateEpicStatus(epic);
        }
        this.subtasks.clear();
    }

    /* End Delete */

    public void deleteTask(Task task){
        tasks.remove(task);
    }

    public Epic getEpicById(int id) {
        if (this.epics.containsKey(id)) {
            return this.epics.get(id);
        }
        return null;
    }

    public void updateEpicStatus(Epic epic){
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean isNew = false;
        boolean isInProgress = false;
        boolean isDone = false;

        ArrayList<Integer> epicSubtasksIds = epic.getSubtaskIds();

        for(int i = 0; i < epicSubtasksIds.size(); i++){
            Subtask curSubtask = this.getSubtaskById(epicSubtasksIds.get(i));
            TaskStatus status = curSubtask.getStatus();
            if(status == TaskStatus.NEW){
                isNew = true;
            }
            else if (status == TaskStatus.IN_PROGRESS) {
                isInProgress = true;
            }
            else if (status == TaskStatus.DONE){
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

    public Subtask getSubtaskById(int id) {
        if (this.subtasks.containsKey(id)) {
            return this.subtasks.get(id);
        }
        return null;
    }
}

