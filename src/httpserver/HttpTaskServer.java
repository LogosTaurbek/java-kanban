package httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static Gson taskAdaptedGson;
    private final TaskManager taskManager;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        taskAdaptedGson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .serializeNulls()
                .create();
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        } catch (IOException e) {
            System.out.println("Ошибка при запуска сервера: " + e);
        }
        this.httpServer.createContext("/tasks", new TaskHandler(taskManager));
        this.httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        this.httpServer.createContext("/epics", new EpicsHandler(taskManager));
        this.httpServer.createContext("/history", new HistoryHandler(taskManager));
        this.httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public static Gson getTaskAdaptedGson() {
        return taskAdaptedGson;
    }

    public void start() {
        this.httpServer.start();
    }

    public void stop() {
        this.httpServer.stop(5);
    }

}
