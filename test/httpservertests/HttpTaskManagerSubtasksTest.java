package httpservertests;

import com.google.gson.Gson;
import httpserver.HttpTaskServer;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerSubtasksTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson taskAdaptedGson = HttpTaskServer.getTaskAdaptedGson();

    public HttpTaskManagerSubtasksTest() {
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
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        int epicId = manager.getEpics().getLast().getId();
        Subtask subtask = new Subtask("subtask", "description", epicId);
        manager.createSubtask(subtask);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        String expectedString = manager.getSubtasks().toString();
        assertEquals(expectedString, response.body());
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        int epicId = manager.getEpics().getLast().getId();
        Subtask subtask = new Subtask("subtask", "description", epicId);
        manager.createSubtask(subtask);
        int subtaskId = manager.getSubtasks().getLast().getId();
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId);
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        String expectedString = manager.getSubtasks().getLast().toString();
        assertEquals(expectedString, response.body());
    }

    @Test
    public void testPostNewSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        int epicId = manager.getEpics().getLast().getId();
        Subtask subtask = new Subtask("subtask", "description", epicId);
        String subtaskJson = taskAdaptedGson.toJson(subtask);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getSubtasks().size());
    }

    @Test
    public void testPostUpdatedSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        int epicId = manager.getEpics().getLast().getId();
        Subtask subtask = new Subtask("subtask", "description", epicId);
        manager.createSubtask(subtask);
        int subtaskId = manager.getSubtasks().getLast().getId();
        Subtask updatedSubtask = new Subtask(subtaskId, "subtask", "description", TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofHours(1), epicId);
        String updatedSubtaskJson = taskAdaptedGson.toJson(updatedSubtask);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getSubtasks().size());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        int epicId = manager.getEpics().getLast().getId();
        Subtask subtask = new Subtask("subtask", "description", epicId);
        manager.createSubtask(subtask);
        int subtaskId = manager.getSubtasks().getLast().getId();
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId);
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getTasks().size());
    }
}
