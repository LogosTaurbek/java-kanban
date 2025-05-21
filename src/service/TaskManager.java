package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void removeALlTasks();

    void removeALlEpics();

    void removeAllSubtasks();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    int createTask(Task newTask);

    int createSubtask(Subtask newSubtask);

    int createEpic(Epic newEpic);

    void updateTask(Task newTask);

    void updateSubtask(Subtask newSubtask);

    void updateEpic(Epic newEpic);

    void removeTaskById(int idToRemove);

    void removeSubtaskById(int idToRemove);

    void removeEpicById(int idToRemove);

    List<Subtask> getSubtasksByEpicId(int epicId);

    HashMap<Integer, Task> getHistory();
}
