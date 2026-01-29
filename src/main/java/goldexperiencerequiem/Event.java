package goldexperiencerequiem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event task.
 */
public class Event extends Task {

    private static final String EVENT_TYPE_ICON = "E";
    private static final String FROM_PREFIX = " (from: ";
    private static final String TO_PREFIX = " to: ";
    private static final String SUFFIX = ")";
    private static final String FILE_DELIMITER = " | ";
    private static final String DATE_OUTPUT_FORMAT = "MMM d yyyy";

    protected LocalDate from;
    protected LocalDate to;

    /**
     * Creates a new Event task with the given description and time range.
     *
     * @param description The description of the event.
     * @param from        The start date of the event.
     * @param to          The end date of the event.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the string representation of the event task.
     *
     * @return The event task icon, status icon, description, from date, and to
     *         date.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_OUTPUT_FORMAT);
        return "[" + EVENT_TYPE_ICON + "]" + super.toString() + FROM_PREFIX
                + from.format(formatter) + TO_PREFIX + to.format(formatter) + SUFFIX;
    }

    /**
     * Returns the file-friendly string representation of the event task.
     *
     * @return File format string.
     */
    @Override
    public String toFileFormat() {
        return EVENT_TYPE_ICON + FILE_DELIMITER + super.toFileFormat() + FILE_DELIMITER + from + FILE_DELIMITER + to;
    }
}
