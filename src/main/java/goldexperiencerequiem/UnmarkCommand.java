package goldexperiencerequiem;

/**
 * Represents a command to mark a task as undone in the task list.
 */
public class UnmarkCommand extends Command {
    private final int index;

    public UnmarkCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RequiemException {
        if (index < 0 || index >= tasks.size()) {
            throw new RequiemException("Task index out of bounds.");
        }
        Task task = tasks.getTask(index);
        task.markAsUndone();
        storage.save(tasks.getAllTasks());
        ui.showTaskUnmarked(task);
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
