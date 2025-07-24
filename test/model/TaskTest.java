package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Test
    void testTasksEquals() {
        Task task1 = new Task(1, "name1", "description1", TaskStatus.NEW);
        Task task2 = new Task(1, "name2", "description2", TaskStatus.IN_PROGRESS);
        assertEquals(task1, task2);
    }

    @Test
    void testTimeRelatedFields() {
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0);
        Duration duration = Duration.ofHours(1);
        Task task = new Task(1, "name1", "description1", TaskStatus.NEW, startTime, duration);
        LocalDateTime newStartTime = LocalDateTime.of(2025, 6, 5, 12, 34);
        task.setStartTime(newStartTime);
        assertEquals(newStartTime, task.getStartTime());
        Duration newDuration = Duration.ofHours(2);
        task.setDuration(newDuration);
        assertEquals(newDuration, task.getDuration());
        LocalDateTime expectedEndTime = LocalDateTime.of(2025, 6, 5, 14, 34);
        assertEquals(expectedEndTime, task.getEndTime());
    }
}