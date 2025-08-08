package model;

import history.HistoryManager;
import history.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;
import service.FileBackedTaskManager;

public class Managers {
    public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public TaskManager getFileBackedTaskManager(String backUpFileName) {
        return new FileBackedTaskManager(backUpFileName);
    }

    public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
