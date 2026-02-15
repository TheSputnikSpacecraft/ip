package goldexperiencerequiem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toString_todoTask_correctFormat() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toFileFormat_todoTask_correctFormat() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    public void equals_sameDescription_returnsTrue() {
        Todo todo1 = new Todo("read book");
        Todo todo2 = new Todo("read book");
        assertEquals(todo1, todo2);
    }

    @Test
    public void equals_differentDescription_returnsFalse() {
        Todo todo1 = new Todo("read book");
        Todo todo2 = new Todo("write code");
        org.junit.jupiter.api.Assertions.assertNotEquals(todo1, todo2);
    }
}
