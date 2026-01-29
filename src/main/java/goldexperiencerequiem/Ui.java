package goldexperiencerequiem;

import java.util.Scanner;

/**
 * Handles interactions with the user.
 */
public class Ui {
    private final Scanner scanner;
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

    /**
     * Initializes the user interface.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the welcome message to the user.
     */
    public void showWelcome() {
        showLine();
        System.out.println(GREETING_MESSAGE);
        showLine();
    }

    /**
     * Displays the divider line to the user.
     */
    public void showLine() {
        System.out.println(LINE_DIVIDER);
    }

    /**
     * Reads a command from the user's input.
     *
     * @return The raw input string.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to print.
     */
    public void showError(String message) {
        System.out.println(ERROR_PREFIX + message);
    }

    /**
     * Displays a loading error message to the user.
     */
    public void showLoadingError() {
        System.out.println(LOADING_ERROR_MESSAGE);
    }

    /**
     * Displays the exit message to the user.
     */
    public void showExit() {
        System.out.println(EXIT_MESSAGE);
    }

    /**
     * Displays that a task has been added.
     *
     * @param task       The task that was added.
     * @param totalTasks The total number of tasks after adding.
     */
    public void showTaskAdded(Task task, int totalTasks) {
        System.out.println(MESSAGE_TASK_ADDED);
        System.out.println("   " + task);
        System.out.println(String.format(MESSAGE_TASKS_COUNT, totalTasks));
    }

    /**
     * Displays that a task has been deleted.
     *
     * @param task       The task that was removed.
     * @param totalTasks The total number of tasks after deletion.
     */
    public void showTaskDeleted(Task task, int totalTasks) {
        System.out.println(MESSAGE_TASK_DELETED);
        System.out.println("   " + task);
        System.out.println(String.format(MESSAGE_TASKS_COUNT, totalTasks));
    }

    /**
     * Displays all tasks in the list.
     *
     * @param tasks The task list to display.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(MESSAGE_TASK_LIST);
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.getTask(i));
        }
    }

    /**
     * Displays that a task has been marked as done.
     *
     * @param task The task that was marked.
     */
    public void showTaskMarked(Task task) {
        System.out.println(MESSAGE_TASK_MARKED);
        System.out.println("   " + task);
    }

    /**
     * Displays that a task has been marked as undone.
     *
     * @param task The task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(MESSAGE_TASK_UNMARKED);
        System.out.println("   " + task);
    }

    /**
     * Displays the matching tasks for a find command.
     *
     * @param matchingTasks The list of tasks that match the keyword.
     */
    public void showMatchingTasks(java.util.ArrayList<Task> matchingTasks) {
        System.out.println(" Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + matchingTasks.get(i));
        }
    }

    /**
     * Closes the scanner used for reading commands.
     */
    public void close() {
        scanner.close();
    }
}
