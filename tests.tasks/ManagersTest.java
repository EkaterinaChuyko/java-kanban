import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import tasks.Managers;

class ManagersTest {

    @Test
    void getDefaultHistoryReturnsNonNullInstance() {
        assertNotNull(Managers.getDefaultHistory(), "HistoryManager должен быть проинициализирован");
    }

    @Test
    void getDefaultReturnsNonNullTaskManager() {
        assertNotNull(Managers.getDefault(), "TaskManager должен быть проинициализирован");
    }
}