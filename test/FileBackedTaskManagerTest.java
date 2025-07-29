import tasks.*;

import org.junit.jupiter.api.Test;
import tasks.TaskManagerTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private static final String TEST_FILE = "test_tasks.csv";

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(new File(TEST_FILE));
    }

    @Test
    void testSaveAndLoad() throws IOException {
        FileBackedTaskManager manager = createManager();
        Task task = new Task("Task", "Desc", Status.NEW);
        manager.createTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(new File(TEST_FILE));
        Task loadedTask = loaded.getTask(task.getId());

        assertEquals(task.getTitle(), loadedTask.getTitle());
        assertEquals(task.getDescription(), loadedTask.getDescription());

        Files.deleteIfExists(new File(TEST_FILE).toPath());
    }

    @Test
    void testExceptionOnInvalidFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager(new File("/invalid/path.csv"));
        Task task = new Task("Task", "Desc", Status.NEW);

        assertThrows(RuntimeException.class, () -> manager.createTask(task));
    }
}