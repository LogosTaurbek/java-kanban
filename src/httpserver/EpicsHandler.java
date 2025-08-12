package httpserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        switch (this.getEndpoint(exchange.getRequestMethod(), requestPath)) {
            case EpicsEndpoint.GET: {
                String result = this.taskManager.getEpics().toString();
                this.sendText(exchange, result);
                break;
            }
            case EpicsEndpoint.GET_ID: {
                int epicId = this.getTaskIdFromRequestPath(requestPath);
                Epic epic = this.taskManager.getEpicById(epicId);
                if (epic == null) {
                    this.sendNotFound(exchange, "Запрошенный эпик не найден.");
                } else {
                    this.sendText(exchange, epic.toString());
                }
                break;
            }
            case EpicsEndpoint.GET_SUBTASKS: {
                int epicId = this.getTaskIdFromRequestPath(requestPath);
                Epic epic = this.taskManager.getEpicById(epicId);
                if (epic == null) {
                    this.sendNotFound(exchange, "Запрошенный Эпик не найден.");
                } else {
                    this.sendText(exchange, this.taskManager.getSubtasksOfEpic(epic).toString());
                }
                break;
            }
            case EpicsEndpoint.POST: {
                // получить данные,
                InputStream requestBody = exchange.getRequestBody();
                String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                // конвертировать их в Subtask,
                Gson taskAdaptedGson = HttpTaskServer.getTaskAdaptedGson();
                Epic epic = taskAdaptedGson.fromJson(body, Epic.class);
                // проверить есть ли ID,
                // вызвать нужный метод
                if (epic.getId() == 0) {
                    int newEpicId = this.taskManager.createEpic(epic);
                    this.sendCreatedWithId(exchange, String.valueOf(newEpicId));

                } else {
                    this.taskManager.updateEpic(epic);
                    this.sendCreated(exchange);
                }
                break;
            }
            case EpicsEndpoint.DELETE_ID: {
                int epicId = this.getTaskIdFromRequestPath(requestPath);
                this.taskManager.removeEpicById(epicId);
                this.sendText(exchange, "Эпик с ID " + epicId + " удален.");
                break;
            }
            case EpicsEndpoint.UNKNOWN: {
                this.sendBadRequest(exchange, "Получен неверный запрос.");
                break;
            }
        }
    }

    private EpicsEndpoint getEndpoint(String requestMethod, String requestPath) {
        switch (requestMethod) {
            case "GET": {
                if (this.pathContainsIdAndSubtasks(requestPath)) {
                    return EpicsEndpoint.GET_SUBTASKS;
                } else if (this.pathContainsId(requestPath)) {
                    return EpicsEndpoint.GET_ID;
                } else if (!this.pathContainsParams(requestPath)) {
                    return EpicsEndpoint.GET;
                } else {
                    return EpicsEndpoint.UNKNOWN;
                }
            }
            case "POST": {
                if (this.pathContainsParams(requestPath)) {
                    return EpicsEndpoint.UNKNOWN;
                } else {
                    return EpicsEndpoint.POST;
                }
            }
            case "DELETE": {
                if (this.pathContainsId(requestPath)) {
                    return EpicsEndpoint.DELETE_ID;
                } else {
                    return EpicsEndpoint.UNKNOWN;
                }
            }
            default:
                return EpicsEndpoint.UNKNOWN;
        }
    }

    private boolean pathContainsIdAndSubtasks(String requestPath) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length != 4) {
            return false;
        }
        try {
            int id = Integer.parseInt(pathParts[2]);
        } catch (NumberFormatException e) {
            return false;
        }
        return pathParts[3].equals("subtasks");
    }

    enum EpicsEndpoint { GET, GET_ID, GET_SUBTASKS, POST, DELETE_ID, UNKNOWN }
}
