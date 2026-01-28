package main.java.goldexperiencerequiem;
/**
 * Represents a command to mark a task as done in the task list.
 */
public class MarkCommand extends Command {
    private final int index;

    public MarkCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RequiemException {
        if (index < 0 || index >= tasks.size()) {
            throw new RequiemException("Task index out of bounds.");
        }
        Task task = tasks.getTask(index);
        task.markAsDone();
        storage.save(tasks.getAllTasks());
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
