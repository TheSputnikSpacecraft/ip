/**
 * Represents a deadline task.
 */
public class Deadline extends Task {

    protected String by;

    /**
     * Creates a new Deadline task with the given description and by date/time.
     *
     * @param description The description of the deadline.
     * @param by          The date/time the deadline is by.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the string representation of the deadline task.
     *
     * @return The string representation of the deadline task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
