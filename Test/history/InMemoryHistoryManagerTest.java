package history;
import model.Managers;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import service.TaskManager;

import java.util.ArrayList;

public class InMemoryHistoryManagerTest {
    @Test
    void hisotryManagerLinksTask() {
        Managers taskManagerUtil = new Managers();
        TaskManager taskManager = taskManagerUtil.getDefault();
        Task task = new Task("name v1", "descriptoin");
        int taskId = taskManager.createTask(task);
        Task taskV1 = taskManager.getTaskById(taskId);

        ArrayList<Task> history = taskManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task, history.getLast());
    }

    @Test
    void historyManagerRemoveTask() {
        Managers taskManagerUtil = new Managers();
        TaskManager taskManager = taskManagerUtil.getDefault();
        Task task = new Task("name v1", "description");
        int taskId = taskManager.createTask(task);
        Task taskV1 = taskManager.getTaskById(taskId);
        taskManager.removeTaskById(taskId);

        ArrayList<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void historyManagerDoesNotDuplicateTask() {
        Managers taskManagerUtil = new Managers();
        TaskManager taskManager = taskManagerUtil.getDefault();
        Task task = new Task("name v1", "description");
        int taskId = taskManager.createTask(task);
        Task taskV1 = taskManager.getTaskById(taskId);
        Task task_update = new Task( task.getId(), task.getName(), task.getDescription(), TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task_update);
        Task taskV2 = taskManager.getTaskById(taskId);

        ArrayList<Task> history = taskManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task, history.getLast());
    }
}
