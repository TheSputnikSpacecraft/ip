package goldexperiencerequiem;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
    public void parse_helpCommand_returnsHelpCommand() throws RequiemException {
        Command command = Parser.parse("help");
        assertTrue(command instanceof HelpCommand);
    }

    @Test
    public void parse_eventInvalidDateOrder_throwsRequiemException() {
        assertThrows(RequiemException.class, () -> Parser.parse("event bad /from 2023-12-01 /to 2023-01-01"));
    }

    @Test
    public void parse_deadlineInvalidDateFormat_throwsRequiemException() {
        assertThrows(RequiemException.class, () -> Parser.parse("deadline bad /by 2023-13-45"));
    }

    @Test
    public void parse_todoEmptyDescription_throwsRequiemException() {
        assertThrows(RequiemException.class, () -> Parser.parse("todo"));
    }

    @Test
    public void parse_eventMissingTo_throwsRequiemException() {
        assertThrows(RequiemException.class, () -> Parser.parse("event bad /from 2023-12-01"));
    }
}
