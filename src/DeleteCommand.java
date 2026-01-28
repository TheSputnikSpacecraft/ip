/**
 * Represents a command to delete a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int index;

    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RequiemException {
        if (index < 0 || index >= tasks.size()) {
            throw new RequiemException("Task index out of bounds.");
        }
        Task removedTask = tasks.deleteTask(index);
        storage.save(tasks.getAllTasks());
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + removedTask);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
