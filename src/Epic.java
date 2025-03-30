import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Integer> subtasksIds = new ArrayList<>();

    Epic(String name, String description){
        super(name, description);
        this.subtasksIds = new ArrayList<>();
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    @Override
    public String toString() {
        String str = "Epic{";
        str += "title='" + this.getName() + "'";
        str += ", description='" + this.getDescription() + "'";
        str += ", status='" + this.getStatus() + "'";
        str += ", subtasksIds='" + this.subtasksIds + "'";
        str += "}";
        return str;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtasksIds;
    }

    void addSubtaskId(int subtaskId){
        this.subtasksIds.add(subtaskId);
    }

    void removeSubtaskId(int subtaskIdToRemove){
        this.subtasksIds.remove((Integer) subtaskIdToRemove);
    }

}
