package goldexperiencerequiem;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles saving and loading tasks from a file.
 */
public class Storage {

    private static final String DEFAULT_DIRECTORY = "data";
    private static final String FILE_DELIMITER = " \\| ";
    private static final String DONE_INDICATOR = "1";
    private static final String TODO_INDICATOR = "T";
    private static final String DEADLINE_INDICATOR = "D";
    private static final String EVENT_INDICATOR = "E";

    private static final int MIN_PARTS_COUNT = 3;
    private static final int DEADLINE_PARTS_COUNT = 4;
    private static final int EVENT_PARTS_COUNT = 5;
    private static final int SPLIT_LIMIT = 5;

    private static final String ERROR_SAVE_FAILED = " Error saving tasks: ";
    private static final String ERROR_LOAD_FAILED = " Error loading tasks: ";

    private final Path path;

    /**
     * Initializes the Storage with a file path.
     *
     * @param fileName Name of the data file.
     */
    public Storage(String fileName) {
        this.path = Paths.get(DEFAULT_DIRECTORY, fileName);
    }

    /**
     * Saves the list of tasks to the file.
     *
     * @param tasks List of tasks to save.
     */
    public void save(ArrayList<Task> tasks) {
        try {
            if (path.getParent() != null && !Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            FileWriter writer = new FileWriter(path.toFile());
            for (Task task : tasks) {
                writer.write(task.toFileFormat() + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println(ERROR_SAVE_FAILED + e.getMessage());
        }
    }

    /**
     * Loads tasks from the file.
     *
     * @return List of tasks loaded from the file.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(path)) {
            return tasks;
        }

        try {
            Scanner scanner = new Scanner(path);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) {
                    continue;
                }
                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println(ERROR_LOAD_FAILED + e.getMessage());
        }

        return tasks;
    }

    /**
     * Parses a single line from the storage file into a Task object.
     *
     * @param line The line to parse.
     * @return The corresponding Task object, or null if the line is corrupted.
     */
    private Task parseTask(String line) {
        try {
            // Use split with limit to handle descriptions that might contain " | "
            String[] parts = line.split(FILE_DELIMITER, SPLIT_LIMIT);
            if (parts.length < MIN_PARTS_COUNT) {
                return null;
            }

            String type = parts[0];
            boolean isDone = parts[1].equals(DONE_INDICATOR);
            String description = parts[2];
            Task task = null;

            switch (type) {
                case TODO_INDICATOR:
                    task = new Todo(description);
                    break;
                case DEADLINE_INDICATOR:
                    if (parts.length >= DEADLINE_PARTS_COUNT) {
                        try {
                            LocalDate date = LocalDate.parse(parts[3]);
                            task = new Deadline(description, date);
                        } catch (DateTimeParseException e) {
                            // Skip corrupted date
                        }
                    }
                    break;
                case EVENT_INDICATOR:
                    if (parts.length >= EVENT_PARTS_COUNT) {
                        try {
                            LocalDate fromDate = LocalDate.parse(parts[3]);
                            LocalDate toDate = LocalDate.parse(parts[4]);
                            task = new Event(description, fromDate, toDate);
                        } catch (DateTimeParseException e) {
                            // Skip corrupted date
                        }
                    }
                    break;
                default:
                    break;
            }

            if (task != null && isDone) {
                task.markAsDone();
            }
            return task;
        } catch (Exception e) {
            // Handle corruption by skipping the line
            return null;
        }
    }
}