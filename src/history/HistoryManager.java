package history;

import model.Task;
import model.Node;

import java.util.HashMap;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    HashMap<Integer, Task> getHistory();
}