package goldexperiencerequiem;

import java.util.Scanner;

/**
 * Handles interactions with the user.
 */
public class Ui {
    private final Scanner scanner;
    private static final String LINE = "____________________________________________________________";

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prints the welcome message to the user.
     */
    public void showWelcome() {
        showLine();
        System.out.println(" Hello! I'm Requiem");
        System.out.println(" What can I do for you?");
        showLine();
    }

    /**
     * Prints the divider line to the user.
     */
    public void showLine() {
        System.out.println(LINE);
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
     * Prints an error message to the user.
     *
     * @param message The error message to print.
     */
    public void showError(String message) {
        System.out.println(" Error: " + message);
    }

    /**
     * Prints a loading error message to the user.
     */
    public void showLoadingError() {
        System.out.println(" Error loading tasks from file.");
    }

    /**
     * Prints the exit message to the user.
     */
    public void showExit() {
        System.out.println(" Bye. Hope to see you again soon!");
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
