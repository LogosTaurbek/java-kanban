package httpservertests;

import com.google.gson.Gson;
import httpserver.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import model.TaskStatus;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest{
    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson taskAdaptedGson = HttpTaskServer.getTaskAdaptedGson();

    public HttpTaskManagerTasksTest() {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = taskAdaptedGson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task("task 1", "description 1");
        Task task2 = new Task("task 2", "description 2");
        manager.createTask(task1);
        manager.createTask(task2);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        String expectedString = manager.getTasks().toString();
        assertEquals(expectedString, response.body());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("task 1", "description 1");
        manager.createTask(task1);
        int taskId = manager.getTasks().getLast().getId();
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/" + taskId);
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        String expectedString = manager.getTasks().getLast().toString();
        assertEquals(expectedString, response.body());
    }

    @Test
    public void testPostNewTask() throws IOException, InterruptedException {
        Task task = new Task("task 1", "description 1");
        String taskJson = taskAdaptedGson.toJson(task);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getTasks().size());
    }

    @Test
    public void testPostUpdatedTask() throws IOException, InterruptedException {
        Task task = new Task("task 1", "description 1");
        manager.createTask(task);
        int taskId = manager.getTasks().getLast().getId();
        Task updatedTask = new Task(taskId, "task 1", "description 1", TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofHours(1));
        String taskJson = taskAdaptedGson.toJson(updatedTask);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getTasks().size());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("task 1", "description 1");
        manager.createTask(task);
        int taskId = manager.getTasks().getLast().getId();
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/" + taskId);
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getTasks().size());
    }
}

