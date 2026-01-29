package goldexperiencerequiem;

/**
 * Represents a command to list all tasks in the task list.
 */
public class ListCommand extends Command {
    /**
     * Executes the list command.
     * Iterates through the task list and displays each task to the user.
     *
     * @param tasks   The TaskList to be displayed.
     * @param ui      The Ui object to interact with the user.
     * @param storage The Storage object (not used in this command).
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
