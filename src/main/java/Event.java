/**
 * Represents an event task.
 */
public class Event extends Task {

    protected String from;
    protected String to;

    /**
     * Creates a new Event task with the given description and time range.
     *
     * @param description The description of the event.
     * @param from        The start time of the event.
     * @param to          The end time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the string representation of the event task.
     *
     * @return The string representation of the event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from + " | " + to;
    }
}
