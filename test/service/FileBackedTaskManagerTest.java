package service;

import model.Epic;
import model.Managers;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    static FileBackedTaskManager taskManager;
    static Task task1;
    static int taskId1;
    static Epic epic1;
    static int epicId1;
    static Subtask subtask1;
    static int subtaskId1;
    static String tmpFilePath;

    @BeforeAll
    static void initManagers() throws ManagerSaveException {
        Managers taskManagersUtil = new Managers();
        try {
            tmpFilePath = File.createTempFile("tasks", ".csv").getPath();
        } catch (IOException e) {
            System.out.println("ОШибка при создании временного файла для тестов.");
        }
        taskManager = (FileBackedTaskManager) taskManagersUtil.getFileBackedTaskManager(tmpFilePath);
        task1 = new Task("task name 1", "task description 1");
        taskId1 = taskManager.createTask(task1);

        epic1 = new Epic("epic name 1", "description");
        epicId1 = taskManager.createEpic(epic1);

        subtask1 = new Subtask("subtask name 1", "Subtask description 1", epicId1);
        subtaskId1 = taskManager.createSubtask(subtask1);
    }

    @Test
    void testCreateTask() throws ManagerSaveException {
        Task task = new Task("name", "description");
        taskManager.createTask(task);
        Task createdTask = taskManager.getTasks().getLast();
        assertEquals(task.getName(), createdTask.getName());
        assertEquals(task.getDescription(), createdTask.getDescription());
    }

    @Test
    void testCreateEpic() throws ManagerSaveException {
        Epic epic = new Epic("epic name 1", "description");
        taskManager.createTask(epic);
        Epic createdEpic = taskManager.getEpics().getLast();

        assertEquals(epic.getName(), createdEpic.getName());
        assertEquals(epic.getDescription(), createdEpic.getDescription());
    }

    @Test
    void testCreateSubtask() throws ManagerSaveException {
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
    void testTaskDoesNotChangeAfterAdding() throws ManagerSaveException {
        Task task = new Task("Task name", "task description");
        int id = taskManager.createTask(task);
        Task taskAdded = taskManager.getTaskById(id);

        assertEquals(task.getName(), taskAdded.getName());
        assertEquals(task.getDescription(), taskAdded.getDescription());
        assertEquals(task.getStatus(), taskAdded.getStatus());
    }

    @Test
    void testSaveLoadEmptyFile() throws ManagerSaveException {

        Managers taskManagerUtil = new Managers();
        try {
            tmpFilePath = File.createTempFile("empty_tasks", ".csv").getPath();
        } catch (IOException e) {
            System.out.println("Error in create temp file for tests");
        }
        FileBackedTaskManager emptyTaskManager = (FileBackedTaskManager) taskManagerUtil.getFileBackedTaskManager(tmpFilePath);
        File fileObj = new File(tmpFilePath);

        assertTrue(fileObj.exists());
        assertEquals(0, fileObj.length());

        FileBackedTaskManager fb = new FileBackedTaskManager(tmpFilePath);
        emptyTaskManager = fb.loadFromFile(fileObj);

        assertNotNull(emptyTaskManager);
        assertNotNull(emptyTaskManager.getTasks());
        assertNotNull(emptyTaskManager.getEpics());
        assertNotNull(emptyTaskManager.getSubtasks());
    }

    @Test
    void testSaveTasks() throws ManagerSaveException {

        Managers taskManagerUtil = new Managers();
        try {
            tmpFilePath = File.createTempFile("empty_tasks", ".csv").getPath();
        } catch (IOException e) {
            System.out.println("Error in create temp file for tests");
        }
        taskManager = (FileBackedTaskManager) taskManagerUtil.getFileBackedTaskManager(tmpFilePath);
        task1 = new Task("task name 1", "task description 1");
        taskId1 = taskManager.createTask(task1);
        Task task2 = new Task("task name 2", "task description 2");
        int taskId2 = taskManager.createTask(task2);
        long linesCount = 0;
        try {
            linesCount = Files.lines(Paths.get(tmpFilePath), Charset.defaultCharset()).count();
        } catch (IOException e) {
            System.out.println("Error in read temp file for test");
        }

        assertEquals(3, linesCount);
    }

    @Test
    void testLoadTasks() throws ManagerSaveException {
        Managers taskManagerUtil = new Managers();
        try {
            tmpFilePath = File.createTempFile("empty_tasks", ".csv").getPath();
        } catch (IOException e) {
            System.out.println("Error in create temp file for tests");
        }

        taskManager = (FileBackedTaskManager) taskManagerUtil.getFileBackedTaskManager(tmpFilePath);
        task1 = new Task("task name 1", "task description 1");
        taskId1 = taskManager.createTask(task1);
        Task task2 = new Task("task name 2", "task description 2");
        int taskId2 = taskManager.createTask(task2);

        File fileObj = new File(tmpFilePath);
        FileBackedTaskManager fb = new FileBackedTaskManager(tmpFilePath);
        taskManager = fb.loadFromFile(fileObj);

        assertEquals(2, taskManager.getTasks().size());
    }
}