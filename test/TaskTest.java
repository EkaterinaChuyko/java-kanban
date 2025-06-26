import tasks.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void tasksAreEqualIfIdEqual() {
        Task task1 = new Task("Title1", "Desc1");
        task1.setId(1);
        Task task2 = new Task("Title2", "Desc2");
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
    }

    @Test
    void subtaskAndEpicAreEqualIfIdEqual() {
        Subtask subtask = new Subtask("Subtask", "desc", 1);
        subtask.setId(10);
        Epic epic = new Epic("Epic", "desc");
        epic.setId(10);
        assertNotEquals(subtask, epic, "Subtask и Epic с одинаковым ID не должны быть равны");
    }
}