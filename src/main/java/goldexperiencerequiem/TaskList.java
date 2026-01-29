package goldexperiencerequiem;

import java.util.ArrayList;

/**
 * Manages the list of tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Initializes an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Initializes a TaskList with an existing list of tasks.
     *
     * @param tasks The initial list of tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the task list.
     *
     * @param task The task to add.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes a task from the list by its index.
     *
     * @param index The index of the task to delete.
     * @return The task that was removed.
     */
    public Task deleteTask(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns a task from the list by its index.
     *
     * @param index The index of the task.
     * @return The task at the specified index.
     */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The size of the task list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a list of tasks that contain the keyword in their description.
     *
     * @param keyword The keyword to search for.
     * @return A list of matching tasks.
     */
    public ArrayList<Task> findTasks(String keyword) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.toString().contains(keyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return The list of tasks.
     */
    public ArrayList<Task> getAllTasks() {
        return tasks;
    }
}
