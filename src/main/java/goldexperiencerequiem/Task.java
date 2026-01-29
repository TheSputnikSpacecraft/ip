package goldexperiencerequiem;
/**
 * Represents a task with a description.
 */
public class Task {
    protected String description;

    protected boolean isDone;

    /**
     * Creates a new Task with the given description.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon of the task (X for done, empty for not done).
     *
     * @return Status icon.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Marks the task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void markAsUndone() {
        this.isDone = false;
    }

    /**
     * Returns the string representation of the task.
     *
     * @return The task status and description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns the string representation of the task for saving to a file.
     *
     * @return File-friendly string representation.
     */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }
}
