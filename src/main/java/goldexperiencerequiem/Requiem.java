package goldexperiencerequiem;

/**
 * Requiem is a personal assistant chatbot.
 * It follows an OOP structure with specialized classes for Ui, Storage,
 * TaskList, and Parser.
 */
public class Requiem {

    private static final String DEFAULT_FILE_NAME = "requiem.txt";

    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    /**
     * Initializes the Requiem assistant with the specified storage file name.
     *
     * @param fileName The name of the file used for storage.
     */
    public Requiem(String fileName) {
        ui = new Ui();
        storage = new Storage(fileName);
        try {
            tasks = new TaskList(storage.load());
        } catch (Exception e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Starts the main loop of the assistant.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;

        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                if (fullCommand.isEmpty()) {
                    continue;
                }
                ui.showLine();
                Command c = Parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (RequiemException e) {
                ui.showError(e.getMessage());
            } catch (Exception e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
        ui.close();
    }

    /**
     * Generates a response for a single user input.
     * <p>
     * This method is intended for GUI usage. It does not run a loop or read from stdin.
     * Instead, it parses and executes exactly one command, buffers all UI output, and
     * returns the result as a string for the GUI to display.
     *
     * @param input The user input string.
     * @return The assistant's response as a single string.
     */
    public String getResponse(String input) {
        ui.enableGuiMode();
        ui.resetBuffer();

        try {
            if (input == null || input.trim().isEmpty()) {
                return "";
            }

            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);

            return ui.getBufferedOutput();
        } catch (RequiemException e) {
            ui.showError(e.getMessage());
            return ui.getBufferedOutput();
        } catch (Exception e) {
            ui.showError(e.getMessage());
            return ui.getBufferedOutput();
        }
    }

    /**
     * Serves as the entry point of the application.
     *
     * @param args The command line arguments (not used).
     */
    public static void main(String[] args) {
        new Requiem(DEFAULT_FILE_NAME).run();
    }
}
