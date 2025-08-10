package httpservertests;

import httpserver.HttpTaskServer;
import model.Epic;
import model.Subtask;
import model.Task;
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

public class HttpTaskManagerPrioritizedTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    public HttpTaskManagerPrioritizedTest() {
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
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("task 1", "description 1", LocalDateTime.now(), Duration.ofHours(1));
        manager.createTask(task1);
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        int epicId = manager.getEpics().getLast().getId();
        Subtask subtask = new Subtask("subtask", "description", LocalDateTime.of(2025, 1, 1, 12, 34), Duration.ofHours(1), epicId);
        manager.createSubtask(subtask);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/prioritized");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        String expectedString = manager.getPrioritizedTasks().toString();
        assertEquals(expectedString, response.body());
    }
}