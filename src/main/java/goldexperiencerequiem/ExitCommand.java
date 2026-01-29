package goldexperiencerequiem;

/**
 * Represents a command to exit the application.
 */
public class ExitCommand extends Command {
    /**
     * Executes the exit command.
     * Displays the exit message to the user.
     *
     * @param tasks   The TaskList (not used in this command).
     * @param ui      The Ui object to interact with the user.
     * @param storage The Storage object (not used in this command).
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showExit();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
