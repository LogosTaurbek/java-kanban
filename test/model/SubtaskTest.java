package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    @Test
    void testSubtasksEquals() {
        Subtask subtask1 = new Subtask(1, "name1", "description1", TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask(1, "name2", "description2", TaskStatus.IN_PROGRESS, 2);
        assertEquals(subtask1, subtask2);
    }

    @Test
    void testTimeRelatedFields() {
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 0, 0);
        Duration duration = Duration.ofHours(1);
        Subtask subtask = new Subtask(1, "name1", "description1", TaskStatus.NEW, startTime, duration, 2);
        LocalDateTime newStartTime = LocalDateTime.of(2025, 6, 5, 12, 34);
        subtask.setStartTime(newStartTime);
        assertEquals(newStartTime, subtask.getStartTime());
        Duration newDuration = Duration.ofHours(2);
        subtask.setDuration(newDuration);
        assertEquals(newDuration, subtask.getDuration());
        LocalDateTime expectedEndTime = LocalDateTime.of(2025, 6, 5, 14, 34);
        assertEquals(expectedEndTime, subtask.getEndTime());
    }
}