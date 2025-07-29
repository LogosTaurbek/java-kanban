package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void testIsOverlapped() {
        LocalDateTime startTime1 = LocalDateTime.of(2025, 6, 6, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2025, 6, 6, 1, 30);
        LocalDateTime startTime3 = LocalDateTime.of(2025, 6, 6, 2, 0);
        Duration duration = Duration.ofHours(1);
        Task task1 = new Task(1, "name1", "description1", TaskStatus.NEW, startTime1, duration);
        Task task2 = new Task(2, "name2", "description2",  TaskStatus.NEW, startTime2, duration);
        Task task3 = new Task(3, "name3", "description3", TaskStatus.NEW, startTime3, duration);

        assertFalse(task1.isOverlapped(task3));
        assertFalse(task3.isOverlapped(task1));
        assertTrue(task1.isOverlapped(task2));
        assertTrue(task2.isOverlapped(task1));
        assertTrue(task2.isOverlapped(task3));
        assertTrue(task3.isOverlapped(task2));
    }
}