package goldexperiencerequiem;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void getTask_validIndex_returnsCorrectTask() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("test task");
        taskList.addTask(todo);
        assertEquals(todo, taskList.getTask(0));
    }
}
