package goldexperiencerequiem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void toString_deadlineTask_correctFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.parse("2023-10-15"));
        assertEquals("[D][ ] return book (by: Oct 15 2023)", deadline.toString());
    }

    @Test
    public void toFileFormat_deadlineTask_correctFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.parse("2023-10-15"));
        assertEquals("D | 0 | return book | 2023-10-15", deadline.toFileFormat());
    }

    @Test
    public void equals_sameDescriptionAndDate_returnsTrue() {
        Deadline d1 = new Deadline("return book", LocalDate.parse("2023-10-15"));
        Deadline d2 = new Deadline("return book", LocalDate.parse("2023-10-15"));
        assertEquals(d1, d2);
    }

    @Test
    public void equals_differentDate_returnsFalse() {
        Deadline d1 = new Deadline("return book", LocalDate.parse("2023-10-15"));
        Deadline d2 = new Deadline("return book", LocalDate.parse("2023-10-16"));
        org.junit.jupiter.api.Assertions.assertNotEquals(d1, d2);
    }
}
