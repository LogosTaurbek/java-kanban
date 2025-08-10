package httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (!method.equals("GET")) {
            this.sendMethodNotAllowed(exchange, "/history поддерживает только метод GET.");
        }
        String result = this.taskManager.getHistory().toString();
        this.sendText(exchange, result);
    }
}
