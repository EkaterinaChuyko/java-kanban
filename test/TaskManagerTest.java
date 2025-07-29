package tasks;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected abstract T createManager();

    @Test
    void testCreateAndGetTask() {
        manager = createManager();
        Task task = new Task("Task", "Desc", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(task);
        Task result = manager.getTask(task.getId());
        assertNotNull(result);
        assertEquals(task, result);
    }

    @Test
    void testCreateAndGetEpic() {
        manager = createManager();
        Epic epic = new Epic("Epic", "Desc");
        manager.createEpic(epic);
        Epic result = manager.getEpic(epic.getId());
        assertNotNull(result);
        assertEquals(epic, result);
    }

    @Test
    void testCreateAndGetSubtask() {
        manager = createManager();
        Epic epic = new Epic("Epic", "Desc");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", epic.getId());
        manager.createSubtask(subtask);
        Subtask result = manager.getSubtask(subtask.getId());
        assertNotNull(result);
        assertEquals(subtask, result);
    }

    @Test
    void testEpicStatusAllNew() {
        manager = createManager();
        Epic epic = new Epic("EpicAllNew", "Desc");
        manager.createEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", epic.getId());
        sub1.setStatus(Status.NEW);
        Subtask sub2 = new Subtask("Sub2", "Desc", epic.getId());
        sub2.setStatus(Status.NEW);

        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        assertEquals(Status.NEW, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void testEpicStatusAllDone() {
        manager = createManager();
        Epic epic = new Epic("EpicAllDone", "Desc");
        manager.createEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", epic.getId());
        sub1.setStatus(Status.DONE);
        Subtask sub2 = new Subtask("Sub2", "Desc", epic.getId());
        sub2.setStatus(Status.DONE);

        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void testEpicStatusNewAndDone() {
        manager = createManager();
        Epic epic = new Epic("EpicNewAndDone", "Desc");
        manager.createEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", epic.getId());
        sub1.setStatus(Status.NEW);
        Subtask sub2 = new Subtask("Sub2", "Desc", epic.getId());
        sub2.setStatus(Status.DONE);

        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void testEpicStatusInProgress() {
        manager = createManager();
        Epic epic = new Epic("EpicInProgress", "Desc");
        manager.createEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", epic.getId());
        sub1.setStatus(Status.IN_PROGRESS);
        Subtask sub2 = new Subtask("Sub2", "Desc", epic.getId());
        sub2.setStatus(Status.NEW);

        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void testTimeIntervalsNoOverlap() {
        manager = createManager();

        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task1", "Desc1", Status.NEW, Duration.ofMinutes(30), now);
        Task task2 = new Task("Task2", "Desc2", Status.NEW, Duration.ofMinutes(30), now.plusMinutes(31));

        manager.createTask(task1);
        manager.createTask(task2);
        assertDoesNotThrow(() -> manager.createTask(task2));
    }

    @Test
    void testTimeIntervalsOverlapThrows() {
        manager = createManager();

        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task1", "Desc1", Status.NEW, Duration.ofMinutes(30), now);
        manager.createTask(task1);

        Task task2 = new Task("Task2", "Desc2", Status.NEW, Duration.ofMinutes(30), now.plusMinutes(15));

        assertThrows(IllegalArgumentException.class, () -> manager.createTask(task2));
    }
}