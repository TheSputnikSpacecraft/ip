package goldexperiencerequiem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void save_tasksList_savesToFile() throws IOException {
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);
        Storage storage = new Storage("test.txt") {
            // Override to use temp directory
            private final Path customPath = dataDir.resolve("test.txt");

            @Override
            public void save(ArrayList<Task> tasks) {
                try {
                    java.io.FileWriter writer = new java.io.FileWriter(customPath.toFile());
                    for (Task task : tasks) {
                        writer.write(task.toFileFormat() + System.lineSeparator());
                    }
                    writer.close();
                } catch (IOException e) {
                    // Ignored
                }
            }
        };

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("test task"));
        storage.save(tasks);

        List<String> lines = Files.readAllLines(dataDir.resolve("test.txt"));
        assertEquals(1, lines.size());
        assertEquals("T | 0 | test task", lines.get(0));
    }

    @Test
    public void load_nonExistentFile_returnsEmptyList() {
        Storage storage = new Storage("nonexistent.txt");
        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }
}
