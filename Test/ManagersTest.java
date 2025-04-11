import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    @Test
    void testDefaultTaskManager() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        assertNotNull(taskManager);
        assertNotNull(taskManager.getTasks());
        assertNotNull(taskManager.getEpics());
        assertNotNull(taskManager.getSubtasks());
    }

    @Test
    void testDefaultHistory() {
        Managers managers = new Managers();
        HistoryManager historyManager = managers.getDefaultHistory();
        assertNotNull(historyManager);
        assertNotNull(historyManager.getHistory());
    }
}