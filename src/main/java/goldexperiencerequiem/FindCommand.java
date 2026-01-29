package goldexperiencerequiem;

import java.util.ArrayList;

/**
 * Represents a command to search for tasks by keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a new FindCommand with the given keyword.
     *
     * @param keyword The keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Executes the find command by searching for matching tasks and displaying
     * them.
     *
     * @param tasks   The list of tasks.
     * @param ui      The user interface.
     * @param storage The storage handler.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ArrayList<Task> matchingTasks = tasks.findTasks(keyword);
        ui.showMatchingTasks(matchingTasks);
    }

    /**
     * Returns false as this command does not exit the application.
     *
     * @return false.
     */
    @Override
    public boolean isExit() {
        return false;
    }
}
