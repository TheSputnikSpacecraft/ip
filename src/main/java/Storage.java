import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles saving and loading tasks from a file.
 */
public class Storage {
    private final Path path;

    /**
     * Initializes the Storage with a file path.
     *
     * @param fileName Name of the data file.
     */
    public Storage(String fileName) {
        this.path = Paths.get("data", fileName);
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
            System.out.println(" Error saving tasks: " + e.getMessage());
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
            System.out.println(" Error loading tasks: " + e.getMessage());
        }

        return tasks;
    }

    private Task parseTask(String line) {
        try {
            // Use split with limit to handle descriptions that might contain " | "
            String[] parts = line.split(" \\| ", 5);
            if (parts.length < 3) {
                return null;
            }

            String type = parts[0];
            boolean isDone = parts[1].equals("1");
            String description = parts[2];
            Task task = null;

            switch (type) {
                case "T":
                    task = new Todo(description);
                    break;
                case "D":
                    if (parts.length >= 4) {
                        try {
                            java.time.LocalDate date = java.time.LocalDate.parse(parts[3]);
                            task = new Deadline(description, date);
                        } catch (java.time.format.DateTimeParseException e) {
                            // Skip corrupted date
                        }
                    }
                    break;
                case "E":
                    if (parts.length >= 5) {
                        try {
                            java.time.LocalDate fromDate = java.time.LocalDate.parse(parts[3]);
                            java.time.LocalDate toDate = java.time.LocalDate.parse(parts[4]);
                            task = new Event(description, fromDate, toDate);
                        } catch (java.time.format.DateTimeParseException e) {
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
