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

    @Test
    public void equals_sameDescriptionAndDates_returnsTrue() {
        Event e1 = new Event("meeting", LocalDate.parse("2023-10-15"), LocalDate.parse("2023-10-16"));
        Event e2 = new Event("meeting", LocalDate.parse("2023-10-15"), LocalDate.parse("2023-10-16"));
        assertEquals(e1, e2);
    }

    @Test
    public void equals_differentDates_returnsFalse() {
        Event e1 = new Event("meeting", LocalDate.parse("2023-10-15"), LocalDate.parse("2023-10-16"));
        Event e2 = new Event("meeting", LocalDate.parse("2023-10-15"), LocalDate.parse("2023-10-17"));
        org.junit.jupiter.api.Assertions.assertNotEquals(e1, e2);
    }
}
