package goldexperiencerequiem;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
}
