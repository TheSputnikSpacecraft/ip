package goldexperiencerequiem;

/**
 * Makes sense of user commands.
 */
public class Parser {

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
            case "BYE":
                return new ExitCommand();
            case "LIST":
                return new ListCommand();
            case "MARK":
                return new MarkCommand(parseIndex(words));
            case "UNMARK":
                return new UnmarkCommand(parseIndex(words));
            case "DELETE":
                return new DeleteCommand(parseIndex(words));
            case "TODO":
                return parseTodo(words);
            case "DEADLINE":
                return parseDeadline(fullCommand, words);
            case "EVENT":
                return parseEvent(fullCommand, words);
            case "FIND":
                return parseFind(words);
            default:
                throw new RequiemException("I'm sorry, but I don't know what that means :-(");
        }
    }

    private static int parseIndex(String[] words) throws RequiemException {
        if (words.length < 2) {
            throw new RequiemException("You need to specify a task index.");
        }
        try {
            return Integer.parseInt(words[1]) - 1;
        } catch (NumberFormatException e) {
            throw new RequiemException("The task index must be a number.");
        }
    }

    private static Command parseTodo(String[] words) throws RequiemException {
        if (words.length < 2 || words[1].trim().isEmpty()) {
            throw new RequiemException("The description of a todo cannot be empty.");
        }
        return new AddCommand(new Todo(words[1].trim()));
    }

    private static Command parseDeadline(String fullCommand, String[] words) throws RequiemException {
        if (words.length < 2 || words[1].trim().isEmpty()) {
            throw new RequiemException("The description of a deadline cannot be empty.");
        }
        int byIndex = fullCommand.indexOf("/by");
        if (byIndex == -1) {
            throw new RequiemException("The deadline must have a /by time.");
        }
        String description = fullCommand.substring(9, byIndex).trim();
        if (description.isEmpty()) {
            throw new RequiemException("The description of a deadline cannot be empty.");
        }
        String by = fullCommand.substring(byIndex + 3).trim();
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(by);
            return new AddCommand(new Deadline(description, date));
        } catch (java.time.format.DateTimeParseException e) {
            throw new RequiemException("Invalid date format. Please use yyyy-MM-dd (e.g., 2019-12-02).");
        }
    }

    private static Command parseEvent(String fullCommand, String[] words) throws RequiemException {
        if (words.length < 2 || words[1].trim().isEmpty()) {
            throw new RequiemException("The description of an event cannot be empty.");
        }
        int fromIndex = fullCommand.indexOf("/from");
        int toIndex = fullCommand.indexOf("/to");
        if (fromIndex == -1 || toIndex == -1) {
            throw new RequiemException("The event must have /from and /to times.");
        }
        String description = fullCommand.substring(6, fromIndex).trim();
        if (description.isEmpty()) {
            throw new RequiemException("The description of an event cannot be empty.");
        }
        String from = fullCommand.substring(fromIndex + 5, toIndex).trim();
        String to = fullCommand.substring(toIndex + 3).trim();
        try {
            java.time.LocalDate fromDate = java.time.LocalDate.parse(from);
            java.time.LocalDate toDate = java.time.LocalDate.parse(to);
            return new AddCommand(new Event(description, fromDate, toDate));
        } catch (java.time.format.DateTimeParseException e) {
            throw new RequiemException("Invalid date format. Please use yyyy-MM-dd (e.g., 2019-12-02).");
        }
    }

    private static Command parseFind(String[] words) throws RequiemException {
        if (words.length < 2 || words[1].trim().isEmpty()) {
            throw new RequiemException("You need to specify a keyword to find.");
        }
        return new FindCommand(words[1].trim());
    }
}
