import model.Epic;
import model.Managers;
import model.Subtask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Трекер задач");

        /* Create 2 task */
        Managers taskManagerUtil = new Managers();
        TaskManager taskManager = taskManagerUtil.getDefault();

        Task task1 = new Task("Задача 1", "Описание задачи 1.");
        int task1_id = taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2.");
        int task2_id = taskManager.createTask(task2);

        /*Create epic with 2 subtasks */

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1_id = taskManager.createEpic(epic1);
        Subtask subtask_1_1 = new Subtask("Подзадача 1-1", "Описание подзадачи 1-1.", epic1_id);
        int subtask_1_1_id = taskManager.createSubtask(subtask_1_1);
        Subtask subtask_1_2 = new Subtask("Подзадача 1-2", "Описание подзадачи 1-2.", epic1_id);
        int subtask_1_2_id = taskManager.createSubtask(subtask_1_2);

        /* Create epic with 1 subtask */

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        int epic2_id = taskManager.createEpic(epic2);
        Subtask subtask_2_1 = new Subtask("Подзадача 2-1", "Описание подзадачи 2-1.", epic2_id);
        int subtask_2_1_id = taskManager.createSubtask(subtask_2_1);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        printHistory(taskManager);

    }

    private static void printHistory(TaskManager taskManager) {
        System.out.println("История просмотра задач:");
        for (Task historyItem : taskManager.getHistory()) {
            System.out.println(historyItem);
        }
    }
}
