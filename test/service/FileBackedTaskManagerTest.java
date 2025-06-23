package service;

import model.Epic;
import model.Managers;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import service.FileBackedTaskManager;

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

    @Test
    void testGetTaskById() {
        Task task = taskManager.getTaskById(taskId1);
        assertEquals(task.getName(), task1.getName());
        assertEquals(task.getDescription(), task1.getDescription());
    }

    @Test
    void testGetEpicById() {
        Epic epic = taskManager.getEpicById(epicId1);
        assertEquals(epic.getName(), epic1.getName());
        assertEquals(epic.getDescription(), epic1.getDescription());
    }

    @Test
    void testGetSubtaskById() {
        Subtask subtask = taskManager.getSubtaskById(subtaskId1);
        assertEquals(subtask.getName(), subtask1.getName());
        assertEquals(subtask.getDescription(), subtask1.getDescription());
    }

    @Test
    void testTaskDoesNotChangeAfterAdding() {
        Task task = new Task("Task name", "task description");
        int id = taskManager.createTask(task);
        Task taskAdded = taskManager.getTaskById(id);

        assertEquals(task.getName(), taskAdded.getName());
        assertEquals(task.getDescription(), taskAdded.getDescription());
        assertEquals(task.getStatus(), taskAdded.getStatus());
    }

    @Test
    void testSaveLoadEmptyFile() {
        Managers taskManagerUtil = new Managers();
        try {
            tmpFilePath = File.createTempFile("empty_tasks", ".csv").getPath();
        }
        catch(IOException e){
            System.out.println("Error in create temp file for tests");
        }
        FileBackedTaskManager emptyTaskManager = (FileBackedTaskManager) taskManagerUtil.getFileBackedTaskManager(tmpFilePath);
        File fileObj = new File(tmpFilePath);

        assertTrue(fileObj.exists());
        assertEquals(0, fileObj.length());

        emptyTaskManager = FileBackedTaskManager.loadFromFile(fileObj);

        assertNotNull(emptyTaskManager);
        assertNotNull(emptyTaskManager.getTasks());
        assertNotNull(emptyTaskManager.getEpics());
        assertNotNull(emptyTaskManager.getSubtasks());
    }
}