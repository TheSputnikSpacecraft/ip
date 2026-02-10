package goldexperiencerequiem;

/**
 * Represents a command to delete a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int index;

    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Executes the delete command.
     * Removes the task from the task list, saves the updated list, and displays a
     * confirmation message to the user.
     *
     * @param tasks   The TaskList from which the task will be deleted.
     * @param ui      The Ui object to interact with the user.
     * @param storage The Storage object to save the updated task list.
     * @throws RequiemException If the task index is out of bounds.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RequiemException {
        assert tasks != null : "DeleteCommand: tasks should not be null";
        assert ui != null : "DeleteCommand: ui should not be null";
        assert storage != null : "DeleteCommand: storage should not be null";
        assert index >= 0 : "DeleteCommand: index should be non-negative (parser should enforce this)";

        if (index < 0 || index >= tasks.size()) {
            throw new RequiemException("Task index out of bounds.");
        }

        int oldSize = tasks.size();
        Task removedTask = tasks.deleteTask(index);

        assert removedTask != null : "DeleteCommand: deleteTask should return a task";
        assert tasks.size() == oldSize - 1 : "DeleteCommand: task list size should decrease by 1 after delete";

        storage.save(tasks.getAllTasks());
        ui.showTaskDeleted(removedTask, tasks.size());
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
