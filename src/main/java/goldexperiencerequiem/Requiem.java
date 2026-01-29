package goldexperiencerequiem;

/**
 * Requiem is a personal assistant chatbot.
 * It follows an OOP structure with specialized classes for Ui, Storage,
 * TaskList, and Parser.
 */
public class Requiem {

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

    private static final String DEFAULT_FILE_NAME = "requiem.txt";

    /**
     * Serves as the entry point of the application.
     *
     * @param args The command line arguments (not used).
     */
    public static void main(String[] args) {
        new Requiem(DEFAULT_FILE_NAME).run();
    }
}
