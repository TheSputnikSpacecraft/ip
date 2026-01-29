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
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.getTask(i));
        }
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
