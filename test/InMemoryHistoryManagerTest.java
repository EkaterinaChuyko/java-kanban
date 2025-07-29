package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager history;

    @BeforeEach
    void setUp() {
        history = new InMemoryHistoryManager();
    }

    @Test
    void historyRetainsCorrectVersionOfTask() {
        Task task = new Task("Task", "desc", Status.NEW);
        task.setId(1);
        history.add(task);

        task.setTitle("Modified");

        List<Task> historyList = history.getHistory();
        assertEquals(1, historyList.size());
        assertEquals("Modified", historyList.get(0).getTitle(), "История должна сохранять актуальную ссылку на задачу");
    }

    @Test
    void shouldNotAddNullTask() {
        history.add(null);
        assertTrue(history.getHistory().isEmpty());
    }

    @Test
    void shouldAddAndReturnTasksInCorrectOrder() {
        Task task1 = new Task("Task1", "desc1", Status.NEW);
        Task task2 = new Task("Task2", "desc2", Status.NEW);
        task1.setId(1);
        task2.setId(2);

        history.add(task1);
        history.add(task2);

        List<Task> historyList = history.getHistory();
        assertEquals(2, historyList.size());
        assertEquals(task1, historyList.get(0));
        assertEquals(task2, historyList.get(1));
    }

    @Test
    void shouldMoveTaskToEndWhenReadded() {
        Task task1 = new Task("Task1", "desc1", Status.NEW);
        Task task2 = new Task("Task2", "desc2", Status.NEW);
        task1.setId(1);
        task2.setId(2);

        history.add(task1);
        history.add(task2);
        history.add(task1);

        List<Task> historyList = history.getHistory();
        assertEquals(2, historyList.size());
        assertEquals(task2, historyList.get(0));
        assertEquals(task1, historyList.get(1));
    }

    @Test
    void shouldRemoveTaskById() {
        Task task1 = new Task("Task1", "desc1", Status.NEW);
        Task task2 = new Task("Task2", "desc2", Status.NEW);
        task1.setId(1);
        task2.setId(2);

        history.add(task1);
        history.add(task2);
        history.remove(1);

        List<Task> historyList = history.getHistory();
        assertEquals(1, historyList.size());
        assertEquals(task2, historyList.get(0));
    }

    @Test
    void testEmptyHistory() {
        assertTrue(history.getHistory().isEmpty(), "История должна быть пустой");
    }

    @Test
    void testNoDuplicatesInHistory() {
        Task task = new Task("Task", "Desc", Status.NEW);
        task.setId(1);

        history.add(task);
        history.add(task);

        assertEquals(1, history.getHistory().size(), "Дубликаты не должны добавляться");
    }

    @Test
    void testRemoveFromHistory() {
        Task t1 = new Task("1", "D1", Status.NEW);
        t1.setId(1);
        Task t2 = new Task("2", "D2", Status.NEW);
        t2.setId(2);
        Task t3 = new Task("3", "D3", Status.NEW);
        t3.setId(3);

        history.add(t1);
        history.add(t2);
        history.add(t3);

        history.remove(1);
        assertEquals(2, history.getHistory().size());
        assertFalse(history.getHistory().contains(t1));

        history.remove(2);
        assertEquals(1, history.getHistory().size());
        assertFalse(history.getHistory().contains(t2));

        history.remove(3);
        assertTrue(history.getHistory().isEmpty());
    }
}