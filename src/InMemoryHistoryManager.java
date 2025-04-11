import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    ArrayList<Task> taskHistory;

    public InMemoryHistoryManager() {
        taskHistory = new ArrayList<>();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return this.taskHistory;
    }

    @Override
    public void add(Task task) {
        if (this.taskHistory.size() == 10) {
            this.taskHistory.removeFirst();
        }
        this.taskHistory.add(task);
    }

}
