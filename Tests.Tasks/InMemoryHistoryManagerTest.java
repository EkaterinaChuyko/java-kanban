import Tasks.InMemoryHistoryManager;
import Tasks.Task;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

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

}