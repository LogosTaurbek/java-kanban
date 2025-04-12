package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void testTasksEquals() {
        Task task1 = new Task(1, "name1", "description1", TaskStatus.NEW);
        Task task2 = new Task(1, "name2", "description2", TaskStatus.IN_PROGRESS);
        assertEquals(task1, task2);
    }
}