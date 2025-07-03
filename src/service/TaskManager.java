package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    int createTask(Task newTask) throws ManagerSaveException;

    int createSubtask(Subtask newSubtask) throws ManagerSaveException;

    int createEpic(Epic newEpic) throws ManagerSaveException;

    void updateTask(Task newTask) throws ManagerSaveException;

    void updateSubtask(Subtask newSubtask) throws ManagerSaveException;

    void updateEpic(Epic newEpic) throws ManagerSaveException;

    void removeTaskById(int idToRemove) throws ManagerSaveException;

    void removeSubtaskById(int idToRemove) throws ManagerSaveException;

    void removeEpicById(int idToRemove) throws ManagerSaveException;

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();
}
