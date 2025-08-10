import httpserver.HttpTaskServer;
import model.Managers;

public class Main {

    public static void main(String[] args) {
        Managers taskManagerUtil = new Managers();
        service.TaskManager taskManager = taskManagerUtil.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }
}
