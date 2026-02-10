package goldexperiencerequiem;

/**
 * Represents a command to mark a task as done in the task list.
 */
public class MarkCommand extends Command {
    private final int index;

    /**
     * Creates a new MarkCommand with the given index.
     *
     * @param index The index of the task to be marked as done.
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RequiemException {
        assert tasks != null : "MarkCommand: tasks should not be null";
        assert ui != null : "MarkCommand: ui should not be null";
        assert storage != null : "MarkCommand: storage should not be null";
        assert index >= 0 : "MarkCommand: index should be non-negative (parser should enforce this)";
        if (index < 0 || index >= tasks.size()) {
            throw new RequiemException("Task index out of bounds.");
        }
        Task task = tasks.getTask(index);
        task.markAsDone();
        storage.save(tasks.getAllTasks());
        ui.showTaskMarked(task);
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
