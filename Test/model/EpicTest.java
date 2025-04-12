package model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void testEpicEquals() {
        ArrayList<Integer> subtasks1 = new ArrayList<>();
        subtasks1.add(1);
        ArrayList<Integer> subtasks2 = new ArrayList<>();
        subtasks2.add(2);
        Epic epic1 = new Epic(1, "name1", "description1", TaskStatus.NEW, subtasks1);
        Epic epic2 = new Epic(1, "name2", "description2",  TaskStatus.IN_PROGRESS, subtasks2);
        assertEquals(epic1, epic2);
    }
}