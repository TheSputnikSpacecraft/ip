package goldexperiencerequiem;

/**
 * Lists all available commands and their usage.
 */
public class HelpCommand extends Command {

    /**
     * Executes the help command.
     *
     * @param tasks   The list of tasks.
     * @param ui      The user interface.
     * @param storage The storage handler.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }

    /**
     * Indicates whether the application should exit after this command.
     *
     * @return False, as the application should continue running.
     */
    @Override
    public boolean isExit() {
        return false;
    }
}
