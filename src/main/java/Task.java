/**
 * Represents a task with a description.
 */
public class Task {
    protected String description;

    /**
     * Creates a new Task with the given description.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        this.description = description;
    }

    /**
     * Returns the string representation of the task.
     *
     * @return The task description.
     */
    @Override
    public String toString() {
        return description;
    }
}
