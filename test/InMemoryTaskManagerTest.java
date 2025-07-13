import tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager manager;

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void testCannotCreateSubtaskThatIsItsOwnEpic() {
        Epic epic = manager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask = new Subtask("Subtask", "desc", epic.getId());
        subtask.setId(epic.getId());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.createSubtask(subtask);
        });

        assertEquals("Подзадача не может быть своим же эпиком", exception.getMessage());
    }

    @Test
    void addsAndFindsTasksById() {
        Task task = new Task("Task", "Desc");
        manager.createTask(task);
        Task found = manager.getTask(task.getId());

        assertNotNull(found);
        assertEquals(task, found);
    }

    @Test
    void handlesDifferentTypes() {
        Epic epic = manager.createEpic(new Epic("Epic", "desc"));
        Subtask subtask = manager.createSubtask(new Subtask("Sub", "desc", epic.getId()));
        Task task = manager.createTask(new Task("Task", "desc"));

        assertEquals(epic, manager.getEpic(epic.getId()));
        assertEquals(subtask, manager.getSubtask(subtask.getId()));
        assertEquals(task, manager.getTask(task.getId()));
    }

    @Test
    void customIdAndGeneratedIdDoNotConflict() {
        Task task1 = new Task("Task1", "Desc1");
        task1.setId(100);
        manager.createTask(task1);

        Task task2 = new Task("Task2", "Desc2");
        manager.createTask(task2);

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void addedTaskIsNotModified() {
        Task task = new Task("Original", "Desc");
        manager.createTask(task);

        Task retrieved = manager.getTask(task.getId());
        assertEquals(task.getTitle(), retrieved.getTitle());
        assertEquals(task.getDescription(), retrieved.getDescription());
        assertEquals(task.getStatus(), retrieved.getStatus());
    }

    @Test
    void tasksAreAddedToHistory() {
        Task task = manager.createTask(new Task("T", "D"));
        manager.getTask(task.getId());

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.getFirst());
    }
}