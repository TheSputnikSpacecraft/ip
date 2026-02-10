package goldexperiencerequiem;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles interactions with the user.
 * <p>
 * This UI supports two modes:
 * <ul>
 * <li><b>CLI mode</b>: prints output to the terminal and reads input using
 * {@link Scanner}.</li>
 * <li><b>GUI mode</b>: buffers output internally so the GUI can display
 * it.</li>
 * </ul>
 */
public class Ui {
    private static final String LINE_DIVIDER = "____________________________________________________________";
    private static final String GREETING_MESSAGE = " Hello! I'm Requiem\n What can I do for you?";
    private static final String ERROR_PREFIX = " Error: ";
    private static final String LOADING_ERROR_MESSAGE = " Error loading tasks from file.";
    private static final String EXIT_MESSAGE = " Bye. Hope to see you again soon!";

    private static final String MESSAGE_TASK_ADDED = " Got it. I've added this task:";
    private static final String MESSAGE_TASK_DELETED = " Noted. I've removed this task:";
    private static final String MESSAGE_TASK_LIST = " Here are the tasks in your list:";
    private static final String MESSAGE_TASK_MARKED = " Nice! I've marked this task as done:";
    private static final String MESSAGE_TASK_UNMARKED = " OK, I've marked this task as not done yet:";
    private static final String MESSAGE_TASKS_COUNT = " Now you have %d tasks in the list.";

    private final Scanner scanner;

    /**
     * Stores output text when in GUI mode. In CLI mode, output is printed directly.
     */
    private final StringBuilder buffer = new StringBuilder();

    /**
     * Indicates whether the UI is in GUI mode. If true, output is stored in
     * {@link #buffer}.
     */
    private boolean isGuiMode = false;

    /**
     * Initializes the user interface in CLI mode by default.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Enables GUI mode.
     * <p>
     * In GUI mode, output will be stored internally instead of being printed to the
     * terminal.
     */
    public void enableGuiMode() {
        isGuiMode = true;
    }

    /**
     * Clears any previously buffered output.
     * <p>
     * This should be called before generating a new response in GUI mode.
     */
    public void resetBuffer() {
        buffer.setLength(0);
    }

    /**
     * Returns the currently buffered output as a single string.
     *
     * @return The buffered output with trailing/leading whitespace trimmed.
     */
    public String getBufferedOutput() {
        return buffer.toString().trim();
    }

    /**
     * Outputs lines to the appropriate destination:
     * <ul>
     * <li>CLI mode: prints to {@code System.out}</li>
     * <li>GUI mode: appends to {@link #buffer}</li>
     * </ul>
     *
     * @param lines The lines to output.
     */
    private void printLine(String... lines) {
        for (String line : lines) {
            if (isGuiMode) {
                buffer.append(line).append("\n");
            } else {
                System.out.println(line);
            }
        }
    }

    /**
     * Displays the welcome message to the user.
     */
    public void showWelcome() {
        showLine();
        printLine(GREETING_MESSAGE);
        showLine();
    }

    /**
     * Displays the divider line to the user.
     */
    public void showLine() {
        printLine(LINE_DIVIDER);
    }

    /**
     * Reads a command from the user's input (CLI mode only).
     *
     * @return The raw input string, trimmed.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        printLine(ERROR_PREFIX + message);
    }

    /**
     * Displays a loading error message to the user.
     */
    public void showLoadingError() {
        printLine(LOADING_ERROR_MESSAGE);
    }

    /**
     * Displays the exit message to the user.
     */
    public void showExit() {
        printLine(EXIT_MESSAGE);
    }

    /**
     * Displays that a task has been added.
     *
     * @param task       The task that was added.
     * @param totalTasks The total number of tasks after adding.
     */
    public void showTaskAdded(Task task, int totalTasks) {
        showTaskChange(MESSAGE_TASK_ADDED, task, totalTasks);
    }

    /**
     * Displays that a task has been deleted.
     *
     * @param task       The task that was removed.
     * @param totalTasks The total number of tasks after deletion.
     */
    public void showTaskDeleted(Task task, int totalTasks) {
        showTaskChange(MESSAGE_TASK_DELETED, task, totalTasks);
    }

    /**
     * Helper to display task changes (add/delete).
     *
     * @param message    The message to display.
     * @param task       The task involved.
     * @param totalTasks The total number of tasks.
     */
    private void showTaskChange(String message, Task task, int totalTasks) {
        printLine(message,
                "   " + task,
                String.format(MESSAGE_TASKS_COUNT, totalTasks));
    }

    /**
     * Displays all tasks in the list.
     *
     * @param tasks The task list to display.
     */
    public void showTaskList(TaskList tasks) {
        printLine(MESSAGE_TASK_LIST);
        for (int i = 0; i < tasks.size(); i++) {
            printLine(" " + (i + 1) + "." + tasks.getTask(i));
        }
    }

    /**
     * Displays that a task has been marked as done.
     *
     * @param task The task that was marked.
     */
    public void showTaskMarked(Task task) {
        printLine(MESSAGE_TASK_MARKED, "   " + task);
    }

    /**
     * Displays that a task has been marked as undone.
     *
     * @param task The task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        printLine(MESSAGE_TASK_UNMARKED, "   " + task);
    }

    /**
     * Displays the matching tasks for a find command.
     *
     * @param matchingTasks The list of tasks that match the keyword.
     */
    public void showMatchingTasks(ArrayList<Task> matchingTasks) {
        printLine(" Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            printLine(" " + (i + 1) + "." + matchingTasks.get(i));
        }
    }

    /**
     * Closes the scanner used for reading commands (CLI mode).
     */
    public void close() {
        scanner.close();
    }
}
