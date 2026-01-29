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

    private static final String STATUS_DONE_ICON = "X";
    private static final String STATUS_NOT_DONE_ICON = " ";
    private static final String FILE_DONE_INDICATOR = "1";
    private static final String FILE_NOT_DONE_INDICATOR = "0";
    private static final String FILE_DELIMITER = " | ";

    /**
     * Returns the status icon of the task (X for done, empty for not done).
     *
     * @return The status icon.
     */
    public String getStatusIcon() {
        return (isDone ? STATUS_DONE_ICON : STATUS_NOT_DONE_ICON);
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
        return (isDone ? FILE_DONE_INDICATOR : FILE_NOT_DONE_INDICATOR) + FILE_DELIMITER + description;
    }
}
