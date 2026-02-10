package goldexperiencerequiem;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Makes sense of user commands.
 */
public class Parser {

    private static final String BYE_COMMAND = "BYE";
    private static final String LIST_COMMAND = "LIST";
    private static final String MARK_COMMAND = "MARK";
    private static final String UNMARK_COMMAND = "UNMARK";
    private static final String DELETE_COMMAND = "DELETE";
    private static final String TODO_COMMAND = "TODO";
    private static final String DEADLINE_COMMAND = "DEADLINE";
    private static final String EVENT_COMMAND = "EVENT";

    private static final String DEADLINE_DELIMITER = "/by";
    private static final String EVENT_FROM_DELIMITER = "/from";
    private static final String EVENT_TO_DELIMITER = "/to";

    private static final int DEADLINE_DELIMITER_LENGTH = 3; // "/by".length()
    private static final int EVENT_FROM_DELIMITER_LENGTH = 5; // "/from".length()
    private static final int EVENT_TO_DELIMITER_LENGTH = 3; // "/to".length()

    private static final String ERROR_UNKNOWN_COMMAND = "I'm sorry, but I don't know what that means :-(";
    private static final String ERROR_SPECIFY_INDEX = "You need to specify a task index.";
    private static final String ERROR_INVALID_INDEX = "The task index must be a number.";
    private static final String ERROR_EMPTY_TODO = "The description of a todo cannot be empty.";
    private static final String ERROR_EMPTY_DEADLINE = "The description of a deadline cannot be empty.";
    private static final String ERROR_MISSING_BY = "The deadline must have a /by time.";
    private static final String ERROR_EMPTY_EVENT = "The description of an event cannot be empty.";
    private static final String ERROR_MISSING_FROM_TO = "The event must have /from and /to times.";
    private static final String ERROR_INVALID_DATE = "Invalid date format. Please use yyyy-MM-dd (e.g., 2019-12-02).";

    /**
     * Parses the user input into a Command object.
     *
     * @param fullCommand The raw input string from the user.
     * @return The corresponding Command object.
     * @throws RequiemException If the command is invalid or missing information.
     */
    public static Command parse(String fullCommand) throws RequiemException {
        String[] words = fullCommand.split(" ", 2);
        String commandWord = words[0].toUpperCase();

        switch (commandWord) {
            case BYE_COMMAND:
                return new ExitCommand();
            case LIST_COMMAND:
                return new ListCommand();
            case MARK_COMMAND:
                return new MarkCommand(parseIndex(words));
            case UNMARK_COMMAND:
                return new UnmarkCommand(parseIndex(words));
            case DELETE_COMMAND:
                return new DeleteCommand(parseIndex(words));
            case TODO_COMMAND:
                return parseTodo(words);
            case DEADLINE_COMMAND:
                return parseDeadline(words);
            case EVENT_COMMAND:
                return parseEvent(words);
            case "FIND":
                return parseFind(words);
            default:
                throw new RequiemException(ERROR_UNKNOWN_COMMAND);
        }
    }

    private static int parseIndex(String[] words) throws RequiemException {
        if (words.length < 2) {
            throw new RequiemException(ERROR_SPECIFY_INDEX);
        }
        try {
            return Integer.parseInt(words[1]) - 1;
        } catch (NumberFormatException e) {
            throw new RequiemException(ERROR_INVALID_INDEX);
        }
    }

    private static Command parseTodo(String[] words) throws RequiemException {
        if (words.length < 2 || words[1].trim().isEmpty()) {
            throw new RequiemException(ERROR_EMPTY_TODO);
        }
        return new AddCommand(new Todo(words[1].trim()));
    }

    private static Command parseDeadline(String[] words) throws RequiemException {
        if (words.length < 2 || words[1].trim().isEmpty()) {
            throw new RequiemException(ERROR_EMPTY_DEADLINE);
        }
        String args = words[1];
        int byIndex = args.indexOf(DEADLINE_DELIMITER);
        if (byIndex == -1) {
            throw new RequiemException(ERROR_MISSING_BY);
        }
        String description = args.substring(0, byIndex).trim();
        if (description.isEmpty()) {
            throw new RequiemException(ERROR_EMPTY_DEADLINE);
        }
        String by = args.substring(byIndex + DEADLINE_DELIMITER_LENGTH).trim();
        try {
            LocalDate date = LocalDate.parse(by);
            return new AddCommand(new Deadline(description, date));
        } catch (DateTimeParseException e) {
            throw new RequiemException(ERROR_INVALID_DATE);
        }
    }

    private static Command parseEvent(String[] words) throws RequiemException {
        if (words.length < 2 || words[1].trim().isEmpty()) {
            throw new RequiemException(ERROR_EMPTY_EVENT);
        }
        String args = words[1];
        int fromIndex = args.indexOf(EVENT_FROM_DELIMITER);
        int toIndex = args.indexOf(EVENT_TO_DELIMITER);
        if (fromIndex == -1 || toIndex == -1) {
            throw new RequiemException(ERROR_MISSING_FROM_TO);
        }
        String description = args.substring(0, fromIndex).trim();
        if (description.isEmpty()) {
            throw new RequiemException(ERROR_EMPTY_EVENT);
        }
        String from = args.substring(fromIndex + EVENT_FROM_DELIMITER_LENGTH, toIndex).trim();
        String to = args.substring(toIndex + EVENT_TO_DELIMITER_LENGTH).trim();
        try {
            LocalDate fromDate = LocalDate.parse(from);
            LocalDate toDate = LocalDate.parse(to);
            return new AddCommand(new Event(description, fromDate, toDate));
        } catch (DateTimeParseException e) {
            throw new RequiemException(ERROR_INVALID_DATE);
        }
    }

    private static Command parseFind(String[] words) throws RequiemException {
        if (words.length < 2 || words[1].trim().isEmpty()) {
            throw new RequiemException("You need to specify a keyword to find.");
        }
        return new FindCommand(words[1].trim());
    }
}
// INDENT FIX
