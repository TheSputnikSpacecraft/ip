package goldexperiencerequiem;

/**
 * Represents a command to add a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a new AddCommand with the given task.
     *
     * @param task The task to be added.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Executes the add command.
     * Adds the task to the task list, saves the updated list, and displays a
     * confirmation message to the user.
     *
     * @param tasks   The TaskList to which the task will be added.
     * @param ui      The Ui object to interact with the user.
     * @param storage The Storage object to save the updated task list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RequiemException {
        assert task != null : "AddCommand: task should not be null";
        assert tasks != null : "AddCommand: tasks should not be null";
        assert ui != null : "AddCommand: ui should not be null";
        assert storage != null : "AddCommand: storage should not be null";

        if (tasks.hasDuplicate(task)) {
            throw new RequiemException("We already went over this. Don't make me repeat myself.");
        }

        int oldSize = tasks.size();
        tasks.addTask(task);

        assert tasks.size() == oldSize + 1 : "AddCommand: task list size should increase by 1 after add";

        storage.save(tasks.getAllTasks());
        ui.showTaskAdded(task, tasks.size());
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
