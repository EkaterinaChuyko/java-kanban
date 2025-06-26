import tasks.InMemoryHistoryManager;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void historyRetainsCorrectVersionOfTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task = new Task("Task", "desc");
        task.setId(1);
        historyManager.add(task);

        task.setTitle("Modified");

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals("Modified", history.get(0).getTitle(), "История должна сохранять актуальную ссылку на задачу");
    }

    @Test
    void shouldNotAddNullTask() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldAddAndReturnTasksInCorrectOrder() {
        Task task1 = new Task("Task1", "desc1");
        Task task2 = new Task("Task2", "desc2");
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void shouldMoveTaskToEndWhenReadded() {
        Task task1 = new Task("Task1", "desc1");
        Task task2 = new Task("Task2", "desc2");
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }

    @Test
    void shouldRemoveTaskById() {
        Task task1 = new Task("Task1", "desc1");
        Task task2 = new Task("Task2", "desc2");
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }
}