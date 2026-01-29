package goldexperiencerequiem;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
