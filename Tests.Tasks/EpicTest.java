import Tasks.Epic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void epicsAreEqualIfIdsEqual() {
        Epic epic1 = new Epic("Epic1", "Desc1");
        epic1.setId(1);
        Epic epic2 = new Epic("Epic2", "Desc2");
        epic2.setId(1);

        assertEquals(epic1, epic2, "Эпики с одинаковыми ID должны считаться равными");
    }

    @Test
    void newEpicHasEmptySubtaskList() {
        Epic epic = new Epic("Epic", "Desc");
        assertTrue(epic.getSubtaskIds().isEmpty(), "У нового эпика не должно быть подзадач");
    }
}