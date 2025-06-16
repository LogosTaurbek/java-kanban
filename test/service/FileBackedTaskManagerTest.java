package service;

import model.Epic;
import model.Managers;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    static  FileBackedTaskManager taskManager;
    static Task task1;
    static int taskId1;
    static Epic epic1;
    static int epicId1;
    static Subtask subtask1;
    static int subtaskId1;
    static String tmpFilePath;

    @BeforeAll
    static void initManagers() {
        Managers taskManagersUtil = new Managers();
        try {
            tmpFilePath = File.createTempFile("tasks", ".csv").getPath();
        }
        catch (IOException e){
            System.out.println("ОШибка при создании временного файла для тестов.");
        }
        taskManager = (FileBackedTaskManager) taskManagersUtil.getFileBackedTaskManager(tmpFilePath);
        task1 = new Task("name", "task description");
        taskId1 = taskManager.createTask(task1);

        epic1 = new Epic("Epic name 1", "epic description");
        epicId1 = taskManager.createTask(epic1);

        subtask1 = new Subtask("Subtask name 1", "Subtask description 1", epicId1);
        subtaskId1 = taskManager.createSubtask(subtask1);
    }

    @Test
    void testCreateTask() {
        Task task = new Task("name", "description");
        taskManager.createTask(task);
        Task createdTask = taskManager.getTasks().getLast();
        assertEquals(task.getName(), createdTask.getName());
        assertEquals(task.getDescription(), createdTask.getDescription());
    }

    @Test
    void testCreateEpic() {
        Epic epic = new Epic("name", "description");
        taskManager.createTask(epic);
        Epic createdEpic = taskManager.getEpics().getLast();

        assertEquals(epic.getName(), createdEpic.getName());
        assertEquals(epic.getDescription(), createdEpic.getDescription());
    }

    @Test
    void testCreateSubtask() {
        Subtask subtask = new Subtask("name", "description", epicId1);
        taskManager.createSubtask(subtask);
        Subtask createdSubtask = taskManager.getSubtasks().getLast();

        assertEquals(subtask.getName(), createdSubtask.getName());
        assertEquals(subtask.getDescription(), createdSubtask.getDescription());
    }


}