package model;

import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    @Test
    void testEpicEquals() {
        ArrayList<Integer> subtasks1 = new ArrayList<>();
        subtasks1.add(1);
        ArrayList<Integer> subtasks2 = new ArrayList<>();
        subtasks2.add(2);
        Epic epic1 = new Epic(1, "name1", "description1", TaskStatus.NEW, subtasks1);
        Epic epic2 = new Epic(1, "name2", "description2", TaskStatus.IN_PROGRESS, subtasks2);
        assertEquals(epic1, epic2);
    }

    @Test
    void testTimeRelatedFields() {
        Managers taskManagerUtil = new Managers();
        TaskManager taskManager = taskManagerUtil.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1_id = taskManager.createEpic(epic1);
        LocalDateTime st1StartTime = LocalDateTime.of(2025, 1, 1, 0, 0);
        Duration st1Duration = Duration.ofHours(1);
        LocalDateTime st2StartTime = LocalDateTime.of(2025, 1, 1, 1, 0);
        Duration st2Duration = Duration.ofHours(1);
        Subtask subtask_1_1 = new Subtask("Подзадача 1-1", "Описание подзадачи 1-1.", st1StartTime, st1Duration, epic1_id);
        int subtask_1_1_id = taskManager.createSubtask(subtask_1_1);
        Subtask subtask_1_2 = new Subtask("Подзадача 1-2", "Описание подзадачи 1-2.", st2StartTime, st2Duration, epic1_id);
        int subtask_1_2_id = taskManager.createSubtask(subtask_1_2);
        LocalDateTime expectedStartTime = LocalDateTime.of(2025, 1, 1, 0, 0);
        Duration expectedDuration = Duration.ofHours(2);
        LocalDateTime expectedEndTime = LocalDateTime.of(2025, 1, 1, 2, 0);
        assertEquals(expectedStartTime, epic1.getStartTime());
        assertEquals(expectedDuration, epic1.getDuration());
        assertEquals(expectedEndTime, epic1.getEndTime());
    }
}