package goldexperiencerequiem;

/**
 * Represents an executable command.
 */
public abstract class Command {
    /**
     * Executes the command.
     *
     * @param tasks   The list of tasks.
     * @param ui      The user interface.
     * @param storage The storage handler.
     * @throws RequiemException If an error occurs during execution.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws RequiemException;

    /**
     * Indicates whether the application should exit after this command.
     *
     * @return True if the app should exit, false otherwise.
     */
    public abstract boolean isExit();
}
