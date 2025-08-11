package httpserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        switch (this.getEndpoint(exchange.getRequestMethod(), requestPath)) {
            case SubtasksEndpoint.GET: {
                String result = this.taskManager.getSubtasks().toString();
                this.sendText(exchange, result);
                break;
            }
            case SubtasksEndpoint.GET_ID: {
                int subtaskId = this.getTaskIdFromRequestPath(requestPath);
                Subtask subtask = this.taskManager.getSubtaskById(subtaskId);
                if (subtask == null) {
                    this.sendNotFound(exchange, "Запрошенная подзадача не найдена.");
                } else {
                    this.sendText(exchange, subtask.toString());
                }
                break;
            }
            case SubtasksEndpoint.POST: {
                InputStream requestBody = exchange.getRequestBody();
                String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                Gson taskAdaptedGson = HttpTaskServer.getTaskAdaptedGson();
                Subtask subtask = taskAdaptedGson.fromJson(body, Subtask.class);
                if (subtask.getId() == 0) {
                    int newSubtaskId = this.taskManager.createSubtask(subtask);
                    if (newSubtaskId == -1) {
                        this.sendHasInteractions(exchange, "Срок выполнения новой подзадачи пересекается с существующими задачами или подзадачами.");
                    }
                    this.sendCreated(exchange);
                } else {
                    this.taskManager.updateSubtask(subtask);
                    this.sendCreated(exchange);
                }
                break;
            }
            case SubtasksEndpoint.DELETE_ID: {
                int subtaskId = this.getTaskIdFromRequestPath(requestPath);
                this.taskManager.removeSubtaskById(subtaskId);
                this.sendText(exchange, "Подзадача с ID " + subtaskId + " удалена.");
                break;
            }
            case SubtasksEndpoint.UNKNOWN: {
                this.sendBadRequest(exchange, "Получен неверный запрос.");
                break;
            }
        }
    }

    private SubtasksEndpoint getEndpoint(String requestMethod, String requestPath) {
        switch (requestMethod) {
            case "GET": {
                if (this.pathContainsId(requestPath)) {
                    return SubtasksEndpoint.GET_ID;
                } else if (!this.pathContainsParams(requestPath)) {
                    return SubtasksEndpoint.GET;
                } else {
                    return SubtasksEndpoint.UNKNOWN;
                }
            }
            case "POST": {
                if (this.pathContainsParams(requestPath)) {
                    return SubtasksEndpoint.UNKNOWN;
                } else {
                    return SubtasksEndpoint.POST;
                }
            }
            case "DELETE": {
                if (this.pathContainsId(requestPath)) {
                    return SubtasksEndpoint.DELETE_ID;
                } else {
                    return SubtasksEndpoint.UNKNOWN;
                }
            }
            default:
                return SubtasksEndpoint.UNKNOWN;
        }
    }

    enum SubtasksEndpoint {GET, GET_ID, POST, DELETE_ID, UNKNOWN}
}
