package Tasks;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private final LinkedList<Task> history = new LinkedList<>();
    private final Set<Integer> historyIds = new HashSet<>();

    @Override
    public void add(Task task) {
        if (task == null) return;

        if (historyIds.contains(task.getId())) {
            history.removeIf(t -> t.getId() == task.getId());
            historyIds.remove(task.getId());
        }

        if (history.size() == MAX_HISTORY_SIZE) {
            Task removed = history.removeFirst();
            historyIds.remove(removed.getId());
        }

        history.add(task);
        historyIds.add(task.getId());
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}