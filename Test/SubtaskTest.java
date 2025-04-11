import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    @Test
    void testSubtasksEquals() {
        Subtask subtask1 = new Subtask(1, "name1", "description1", TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask(1, "name2", "description2", TaskStatus.IN_PROGRESS, 2);
        assertEquals(subtask1, subtask2);
    }
}