package goldexperiencerequiem;

/**
 * Represents a command to add a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.addTask(task);
        storage.save(tasks.getAllTasks());
        ui.showTaskAdded(task, tasks.size());
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
