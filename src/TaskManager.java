import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TaskManager {

    private int taskId = 0;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;

    public TaskManager(){
        taskId = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    /* Get */

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

    private Task getTaskById(int id) {
        return this.tasks.get(id);
    }

    private Epic getEpicById(int id) {
        return this.epics.get(id);
    }

    private ArrayList<Subtask> getSubtasksByEpicId(int epicId){
        Epic epic = this.getEpicById(epicId);
        if(epic != null){
            ArrayList<Subtask> subtasks = new ArrayList<>();
            ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
            for(Integer subtaskId: subtaskIds){
                subtasks.add(this.subtasks.get(subtaskId));
            }
            return subtasks;
        }
        else{
            return null;
        }
    }

    private Subtask getSubtaskById(int id) {
        ;return this.subtasks.get(id);
    }

    /* End Get */

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
        if(connectedEpic != null){
            connectedEpic.addSubtaskId(subtask.getId());
            this.updateEpicStatus(connectedEpic);
        }
        return subtask.getId();
    }

    /* End create */

    /* Update */

    public void updateTask(Task task) {
        this.tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        Epic epicToUpdate = this.getEpicById(epic.getId());
        if(epicToUpdate != null){
            epicToUpdate.name = epic.name;
            epicToUpdate.description = epic.description;
            this.updateEpicStatus(epic);

        }
    }

    public void updateSubtask(Subtask subtask) {
        Subtask subtaskToUpdate = this.getSubtaskById(subtask.getId());
        if(subtaskToUpdate == null){
            return;
        }
        Epic epicToUpdate = this.getEpicById(subtask.getEpicId());
        if(epicToUpdate != null) {
            this.subtasks.put(subtask.getId(), subtask);
            this.updateEpicStatus(epicToUpdate);
        }
    }

    private void updateEpicStatus(Epic epic){
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
                this.subtasks.remove(subtaskId);
            }

            this.epics.remove(epicId);
        }
    }

    public void removeSubtaskById(int idToRemove) {
        Subtask subtaskToRemove = this.getSubtaskById(idToRemove);
        if(subtaskToRemove != null) {
            Epic epic = this.getEpicById(subtaskToRemove.getEpicId());
            epic.removeSubtaskId(idToRemove);
            this.updateEpicStatus(epic);
            this.subtasks.remove(idToRemove);
        }
    }

    public void removeALlTasks(){
        ArrayList<Task> tasks = this.getTasks();
        tasks.clear();
    }

    public void removeALlEpics(){
        ArrayList<Epic> epics = this.getEpics();
        epics.clear();
    }

    public void removeAllSubtasks() {
        for (Epic epic : this.getEpics()) {
            epic.removeAllSubtasks();
            this.updateEpicStatus(epic);
        }
        this.subtasks.clear();
    }

    /* End Delete */

}

