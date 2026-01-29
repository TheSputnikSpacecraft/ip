package goldexperiencerequiem;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ParserTest {

    @Test
    public void parse_byeCommand_returnsExitCommand() throws RequiemException {
        Command command = Parser.parse("bye");
        assertTrue(command instanceof ExitCommand);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_todoCommand_returnsAddCommand() throws RequiemException {
        Command command = Parser.parse("todo read book");
        assertTrue(command instanceof AddCommand);
    }

    @Test
    public void parse_deadlineCommandWithDate_returnsAddCommand() throws RequiemException {
        Command command = Parser.parse("deadline return book /by 2023-10-15");
        assertTrue(command instanceof AddCommand);
    }

    @Test
    public void parse_invalidCommand_throwsRequiemException() {
        assertThrows(RequiemException.class, () -> Parser.parse("invalid command"));
    }

    @Test
    public void parse_deadlineMissingBy_throwsRequiemException() {
        assertThrows(RequiemException.class, () -> Parser.parse("deadline return book"));
    }
}
