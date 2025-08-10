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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerHistoryTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    public HttpTaskManagerHistoryTest() {
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
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("task 1", "description 1");
        manager.createTask(task1);
        Epic epic = new Epic("epic", "description");
        manager.createEpic(epic);
        int epicId = manager.getEpics().getLast().getId();
        Subtask subtask = new Subtask("subtask", "description", epicId);
        manager.createSubtask(subtask);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/history");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        String expectedString = manager.getHistory().toString();
        assertEquals(expectedString, response.body());
    }
}
