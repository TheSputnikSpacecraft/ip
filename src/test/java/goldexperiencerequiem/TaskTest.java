package goldexperiencerequiem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void getStatusIcon_notDone_emptySpace() {
        Task task = new Task("test task");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_done_capitalX() {
        Task task = new Task("test task");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsDone_notDone_marksAsDone() {
        Task task = new Task("test task");
        task.markAsDone();
        assertTrue(task.isDone);
    }

    @Test
    public void markAsUndone_done_marksAsUndone() {
        Task task = new Task("test task");
        task.markAsDone();
        task.markAsUndone();
        assertFalse(task.isDone);
    }

    @Test
    public void toString_notDone_correctFormat() {
        Task task = new Task("test task");
        assertEquals("[ ] test task", task.toString());
    }

    @Test
    public void toFileFormat_done_correctFormat() {
        Task task = new Task("test task");
        task.markAsDone();
        assertEquals("1 | test task", task.toFileFormat());
    }

    @Test
    public void equals_sameDescription_returnsTrue() {
        Task task1 = new Task("test task");
        Task task2 = new Task("test task");
        assertEquals(task1, task2);
    }

    @Test
    public void equals_differentDescription_returnsFalse() {
        Task task1 = new Task("test task");
        Task task2 = new Task("other task");
        org.junit.jupiter.api.Assertions.assertNotEquals(task1, task2);
    }

    @Test
    public void hashCode_sameDescription_sameHashCode() {
        Task task1 = new Task("test task");
        Task task2 = new Task("test task");
        assertEquals(task1.hashCode(), task2.hashCode());
    }
}
