import Tasks.InMemoryTaskManager;
import Tasks.InMemoryHistoryManager;
import Tasks.Epic;
import Tasks.Subtask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    void setup() {
        manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        Subtask subtask = new Subtask("Sub", "desc", 1);
        subtask.setId(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.createSubtask(subtask);
        });

        assertEquals("Подзадача не может быть своим же эпиком", exception.getMessage());
    }

    @Test
    void creatingValidSubtaskSucceeds() {
        Epic epic = manager.createEpic(new Epic("Epic", "desc"));
        Subtask subtask = new Subtask("Sub", "desc", epic.getId());

        Subtask created = manager.createSubtask(subtask);

        assertNotNull(created);
        assertEquals(epic.getId(), created.getEpicId());
    }

}