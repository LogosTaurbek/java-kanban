package model;

public class Node {
    protected Node prevNode;
    protected Task task;
    protected Node nextNode;

    public Node (Node prevNode, Task task, Node nextNode) {
            this.prevNode = prevNode;
            this.task = task;
            this.nextNode = nextNode;
    }

    public Node getPrevNode() {
        return prevNode;
    }

    public void setPrevNode(Node prevNode) {
        this.prevNode = prevNode;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Node getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }


}
