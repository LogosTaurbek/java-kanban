package history;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> taskHistory;

    public InMemoryHistoryManager() {
        taskHistory = new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return this.taskHistory;
    }

    @Override
    public void add(Task task) {
        if(task != null){
            if (this.taskHistory.size() == 10) {
                this.taskHistory.removeFirst();
            }
            this.taskHistory.add(task);
        }
    }

}
