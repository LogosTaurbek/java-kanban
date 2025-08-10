package httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (!method.equals("GET")) {
            this.sendMethodNotAllowed(exchange, "/prioritized поддерживает только метод GET.");
        }
        String result = this.taskManager.getPrioritizedTasks().toString();
        this.sendText(exchange, result);
    }
}