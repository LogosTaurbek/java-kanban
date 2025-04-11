import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    @Test
    void historyManagerSavesVersions() {
        Managers taskManagerUtil = new Managers();
        TaskManager taskManager = taskManagerUtil.getDefault();

        Task task = new Task("name v1", "description V1");
        int taskId = taskManager.createTask(task);
        Task taskV1 = taskManager.getTaskById(taskId);
        Task taskUpdate = new Task(taskId,"name v2", "description v2", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(taskUpdate);
        Task taskV2 = taskManager.getTaskById(taskId);

        ArrayList<Task> history = taskManager.getHistory();

        assertEquals(history.get(0).getName(), taskV1.getName());
        assertEquals(history.get(0).getDescription(), taskV1.getDescription());
        assertEquals(history.get(0).getStatus(), taskV1.getStatus());

        assertEquals(history.get(1).getName(), taskV2.getName());
        assertEquals(history.get(1).getDescription(), taskV2.getDescription());
        assertEquals(history.get(1).getStatus(), taskV2.getStatus());
    }
}