package history;

import model.Task;
import model.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private HashMap<Integer, Task> taskHistory;

    public InMemoryHistoryManager() {
        taskHistory = new HashMap<>();
    }

    @Override
    public HashMap<Integer, Task> getHistory() {
        return this.taskHistory;
    }

    @Override
    public void add(Task task) {
        if(task != null){
            if (this.taskHistory.get(task.getId()) != null){
                this.taskHistory.remove(task.getId());
            }
            this.taskHistory.put(0, task);
        }
    }

    @Override
    public void remove(int id){

    }

}
