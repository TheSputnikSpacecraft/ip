package goldexperiencerequiem;

/**
 * Represents a todo task.
 */
public class Todo extends Task {

    /**
     * Creates a new Todo task with the given description.
     *
     * @param description The description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the string representation of the todo task.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns the string representation of the todo task for saving to a file.
     *
     * @return The file-friendly string representation.
     */
    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }
}
