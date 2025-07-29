package tasks;

import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected int nextId = 1;
    protected final HistoryManager historyManager;
    private final NavigableSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(LocalDateTime::compareTo)).thenComparingInt(Task::getId));

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public Task createTask(Task task) {
        if (hasIntersection(task)) {
            throw new IllegalArgumentException("Новая задача пересекается с существующей.");
        }
        task.setId(ensureId(task));
        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(ensureId(epic));
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (hasIntersection(subtask)) {
            throw new IllegalArgumentException("Новая подзадача пересекается с существующей.");
        }
        subtask.setId(ensureId(subtask));
        if (subtask.getId() == subtask.getEpicId()) {
            throw new IllegalArgumentException("Подзадача не может быть своим же эпиком");
        }
        subtasks.put(subtask.getId(), subtask);
        addToPrioritizedTasks(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicFields(epic);
        }
        return subtask;
    }


    @Override
    public void updateTask(Task task) {
        removeFromPrioritizedTasks(task.getId());
        if (hasIntersection(task)) {
            throw new IllegalArgumentException("Обновлённая задача пересекается с существующей.");
        }
        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        removeFromPrioritizedTasks(subtask.getId());
        if (hasIntersection(subtask)) {
            throw new IllegalArgumentException("Обновлённая подзадача пересекается с существующей.");
        }
        subtasks.put(subtask.getId(), subtask);
        addToPrioritizedTasks(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicFields(epic);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic existing = epics.get(epic.getId());
        if (existing != null) {
            existing.setTitle(epic.getTitle());
            existing.setDescription(epic.getDescription());
            updateEpicStatus(existing);
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        removeFromPrioritizedTasks(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subId : epic.getSubtaskIds()) {
                subtasks.remove(subId);
                removeFromPrioritizedTasks(subId);
            }
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicFields(epic);
            }
            removeFromPrioritizedTasks(id);
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        prioritizedTasks.removeIf(task -> task instanceof Task && !(task instanceof Subtask));
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (Integer subId : epic.getSubtaskIds()) {
                subtasks.remove(subId);
                removeFromPrioritizedTasks(subId);
            }
        }
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicFields(epic);
        }
        subtasks.clear();
        prioritizedTasks.removeIf(task -> task instanceof Subtask);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic == null ? List.of() : epic.getSubtaskIds().stream().map(subtasks::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean isTaskTimeIntersect(Task task) {
        return hasIntersection(task);
    }

    private int generateId() {
        return nextId++;
    }

    private int ensureId(Task task) {
        if (task.getId() > 0) {
            nextId = Math.max(nextId, task.getId() + 1);
            return task.getId();
        } else {
            return generateId();
        }
    }

    private void updateEpicStatus(Epic epic) {
        if (epic == null) return;
        List<Integer> subIds = epic.getSubtaskIds();
        if (subIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        int newCount = 0;
        int doneCount = 0;

        for (int id : subIds) {
            Subtask subtask = subtasks.get(id);
            if (subtask != null) {
                switch (subtask.getStatus()) {
                    case NEW -> newCount++;
                    case DONE -> doneCount++;
                }
            }
        }

        if (doneCount == subIds.size()) {
            epic.setStatus(Status.DONE);
        } else if (newCount == subIds.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null && task.getDuration() != null && !prioritizedTasks.contains(task)) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritizedTasks(int id) {
        prioritizedTasks.removeIf(t -> t.getId() == id);
    }

    private void updateEpicFields(Epic epic) {
        List<Subtask> epicSubtasks = getSubtasksByEpic(epic.getId());
        epic.calculateFields(epicSubtasks);
        updateEpicStatus(epic);
    }

    private boolean hasIntersection(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
        }
        return prioritizedTasks.stream().filter(t -> t.getId() != newTask.getId()).filter(t -> t.getStartTime()
                != null && t.getDuration() != null).anyMatch(t -> isOverlapping(newTask, t));
    }

    private boolean isOverlapping(Task t1, Task t2) {
        return !t1.getEndTime().isBefore(t2.getStartTime()) && !t1.getStartTime().isAfter(t2.getEndTime());
    }
}