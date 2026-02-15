package goldexperiencerequiem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void addTask_newTask_increasesSize() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("test task"));
        assertEquals(1, taskList.size());
    }

    @Test
    public void deleteTask_validIndex_decreasesSizeAndReturnsTask() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("test task");
        taskList.addTask(todo);
        Task removedTask = taskList.deleteTask(0);
        assertEquals(0, taskList.size());
        assertEquals(todo, removedTask);
    }

    @Test
    public void deleteTask_invalidIndex_throwsException() {
        TaskList taskList = new TaskList();
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.deleteTask(0));
    }

    @Test
    public void hasDuplicate_duplicateExists_returnsTrue() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("test task");
        taskList.addTask(todo);
        assertTrue(taskList.hasDuplicate(new Todo("test task")));
    }

    @Test
    public void hasDuplicate_noDuplicate_returnsFalse() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("test task"));
        assertFalse(taskList.hasDuplicate(new Todo("other task")));
    }

    @Test
    public void findTasks_matchingKeyword_returnsMatches() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("read book"));
        taskList.addTask(new Todo("write code"));
        java.util.ArrayList<Task> matches = taskList.findTasks("book");
        assertEquals(1, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).toString());
    }
}
