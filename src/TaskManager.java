import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

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

    ArrayList<Subtask> getSubtasksByEpicId(int epicId);

    ArrayList<Task> getHistory();
}
