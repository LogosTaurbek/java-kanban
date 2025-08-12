package httpserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        switch (this.getEndpoint(exchange.getRequestMethod(), requestPath)) {
            case TasksEndpoint.GET: {
                String result = this.taskManager.getTasks().toString();
                this.sendText(exchange, result);
                break;
            }
            case TasksEndpoint.GET_ID: {
                int taskId = this.getTaskIdFromRequestPath(requestPath);
                Task task = this.taskManager.getTaskById(taskId);
                if (task == null) {
                    this.sendNotFound(exchange, "Запрошенная задача не найдена.");
                } else {
                    this.sendText(exchange, task.toString());
                }
                break;
            }
            case TasksEndpoint.POST: {
                // получить данные,
                InputStream requestBody = exchange.getRequestBody();
                String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                // конвертировать их в Task,
                Gson taskAdaptedGson = HttpTaskServer.getTaskAdaptedGson();
                Task task = taskAdaptedGson.fromJson(body, Task.class);
                // проверить есть ли ID,
                // вызвать нужный метод
                int newTaskId = -1;
                if (task.getId() == 0) {
                    try {
                        newTaskId = this.taskManager.createTask(task);
                    }
                    catch(IllegalArgumentException e){
                        this.sendHasInteractions(exchange, "Срок выполнения новой задачи пересекается с существующими задачами или подзадачами.");
                    }
                    this.sendCreatedWithId(exchange, String.valueOf(newTaskId));
                } else {
                    this.taskManager.updateTask(task);
                    this.sendCreated(exchange);
                }
                break;
            }
            case TasksEndpoint.DELETE_ID: {
                int taskId = this.getTaskIdFromRequestPath(requestPath);
                this.taskManager.removeTaskById(taskId);
                this.sendText(exchange, "Задача с ID " + taskId + " удалена.");
                break;
            }
            case TasksEndpoint.UNKNOWN: {
                this.sendBadRequest(exchange, "Получен неверный запрос.");
                break;
            }
        }
    }

    private TasksEndpoint getEndpoint(String requestMethod, String requestPath) {
        switch (requestMethod) {
            case "GET": {
                if (this.pathContainsId(requestPath)) {
                    return TasksEndpoint.GET_ID;
                } else if (!this.pathContainsParams(requestPath)) {
                    return TasksEndpoint.GET;
                } else {
                    return TasksEndpoint.UNKNOWN;
                }
            }
            case "POST": {
                if (this.pathContainsParams(requestPath)) {
                    return TasksEndpoint.UNKNOWN;
                } else {
                    return TasksEndpoint.POST;
                }
            }
            case "DELETE": {
                if (this.pathContainsId(requestPath)) {
                    return TasksEndpoint.DELETE_ID;
                } else {
                    return TasksEndpoint.UNKNOWN;
                }
            }
            default:
                return TasksEndpoint.UNKNOWN;
        }
    }

    enum TasksEndpoint { GET, GET_ID, POST, DELETE_ID, UNKNOWN }

}
