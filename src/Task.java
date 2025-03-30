public class Task {
    private int id;
    private String name;
    private TaskStatus status;
    private String description;

    @Override
    public String toString() {
        String str = "Task{";
        str += "title='" + this.name + "'";
        str += ", description='" + this.description + "'";
        str += ", status='" + this.status + "'";
        str += "}";
        return str;
    }

    public Task(String name, String description){
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(int id, String name, String description, TaskStatus status){
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

}
