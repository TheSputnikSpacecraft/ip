package goldexperiencerequiem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void toString_eventTask_correctFormat() {
        Event event = new Event("project meeting", LocalDate.parse("2023-10-15"), LocalDate.parse("2023-10-16"));
        assertEquals("[E][ ] project meeting (from: Oct 15 2023 to: Oct 16 2023)", event.toString());
    }

    @Test
    public void toFileFormat_eventTask_correctFormat() {
        Event event = new Event("project meeting", LocalDate.parse("2023-10-15"), LocalDate.parse("2023-10-16"));
        assertEquals("E | 0 | project meeting | 2023-10-15 | 2023-10-16", event.toFileFormat());
    }
}
