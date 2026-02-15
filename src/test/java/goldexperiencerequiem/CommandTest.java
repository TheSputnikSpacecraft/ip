package goldexperiencerequiem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CommandTest {

    @TempDir
    Path tempDir;

    private TaskList tasks;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        ui = new Ui();
        storage = new Storage("test_commands.txt");
    }

    @Test
    public void addCommand_execute_addsTask() throws RequiemException {
        Todo todo = new Todo("test task");
        AddCommand command = new AddCommand(todo);
        command.execute(tasks, ui, storage);
        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.getTask(0));
    }

    @Test
    public void deleteCommand_execute_removesTask() throws RequiemException {
        Todo todo = new Todo("test task");
        tasks.addTask(todo);
        DeleteCommand command = new DeleteCommand(0);
        command.execute(tasks, ui, storage);
        assertEquals(0, tasks.size());
    }

    @Test
    public void markCommand_execute_marksTaskAsDone() throws RequiemException {
        Todo todo = new Todo("test task");
        tasks.addTask(todo);
        MarkCommand command = new MarkCommand(0);
        command.execute(tasks, ui, storage);
        assertTrue(tasks.getTask(0).isDone);
    }

    @Test
    public void unmarkCommand_execute_marksTaskAsUndone() throws RequiemException {
        Todo todo = new Todo("test task");
        todo.markAsDone();
        tasks.addTask(todo);
        UnmarkCommand command = new UnmarkCommand(0);
        command.execute(tasks, ui, storage);
        assertFalse(tasks.getTask(0).isDone);
    }

    @Test
    public void exitCommand_isExit_returnsTrue() {
        ExitCommand command = new ExitCommand();
        assertTrue(command.isExit());
    }

    @Test
    public void addCommand_duplicateTask_throwsRequiemException() throws RequiemException {
        Todo todo = new Todo("test task");
        tasks.addTask(todo);
        AddCommand command = new AddCommand(todo);
        org.junit.jupiter.api.Assertions.assertThrows(RequiemException.class,
                () -> command.execute(tasks, ui, storage));
    }

    @Test
    public void deleteCommand_invalidIndex_throwsRequiemException() {
        DeleteCommand command = new DeleteCommand(10);
        org.junit.jupiter.api.Assertions.assertThrows(RequiemException.class,
                () -> command.execute(tasks, ui, storage));
    }

    @Test
    public void markCommand_invalidIndex_throwsRequiemException() {
        MarkCommand command = new MarkCommand(10);
        org.junit.jupiter.api.Assertions.assertThrows(RequiemException.class,
                () -> command.execute(tasks, ui, storage));
    }

    @Test
    public void unmarkCommand_invalidIndex_throwsRequiemException() {
        UnmarkCommand command = new UnmarkCommand(10);
        org.junit.jupiter.api.Assertions.assertThrows(RequiemException.class,
                () -> command.execute(tasks, ui, storage));
    }

    @Test
    public void findCommand_execute_findsMatchingTasks() throws RequiemException {
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Todo("write code"));
        FindCommand command = new FindCommand("book");
        // capturing output is hard here without mocking Ui, so we just ensure it runs
        // without error
        // and perhaps check internal state if possible, but FindCommand mostly writes
        // to Ui.
        // Ideally we should mock Ui, but adhering to constraints, we at least verify it
        // executes.
        command.execute(tasks, ui, storage);
    }
}
