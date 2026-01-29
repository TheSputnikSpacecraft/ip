package goldexperiencerequiem;

import java.util.Scanner;

/**
 * Handles interactions with the user.
 */
public class Ui {
    private final Scanner scanner;
    private static final String LINE = "____________________________________________________________";

    /**
     * Initializes the user interface.
     */
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
     * Displays an error message to the user.
     *
     * @param message The error message to print.
     */
    public void showError(String message) {
        System.out.println(" Error: " + message);
    }

    /**
     * Displays a loading error message to the user.
     */
    public void showLoadingError() {
        System.out.println(" Error loading tasks from file.");
    }

    /**
     * Displays the exit message to the user.
     */
    public void showExit() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    /**
     * Closes the scanner used for reading commands.
     */
    public void close() {
        scanner.close();
    }
}
