package goldexperiencerequiem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a deadline task.
 */
public class Deadline extends Task {

    private static final String DEADLINE_TYPE_ICON = "D";
    private static final String BY_PREFIX = " (by: ";
    private static final String BY_SUFFIX = ")";
    private static final String FILE_DELIMITER = " | ";
    private static final String DATE_OUTPUT_FORMAT = "MMM d yyyy";

    protected LocalDate by;

    /**
     * Creates a new Deadline task with the given description and by date.
     *
     * @param description The description of the deadline.
     * @param by          The date the deadline is by.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the string representation of the deadline task.
     *
     * @return The deadline task icon, status icon, description, and by date.
     */
    @Override
    public String toString() {
        return "[" + DEADLINE_TYPE_ICON + "]" + super.toString() + BY_PREFIX
                + by.format(DateTimeFormatter.ofPattern(DATE_OUTPUT_FORMAT)) + BY_SUFFIX;
    }

    /**
     * Returns the file-friendly string representation of the deadline task.
     *
     * @return File format string.
     */
    @Override
    public String toFileFormat() {
        return DEADLINE_TYPE_ICON + FILE_DELIMITER + super.toFileFormat() + FILE_DELIMITER + by;
    }
}
