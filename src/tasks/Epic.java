package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, Status.NEW, Duration.ZERO, null);
    }

    public void calculateFields(List<Subtask> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            setStartTime(null);
            endTime = null;
            setDuration(Duration.ZERO);
            return;
        }

        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;
        Duration totalDuration = Duration.ZERO;

        for (Subtask sub : subtasks) {
            if (sub.getStartTime() != null && sub.getDuration() != null) {
                if (earliestStart == null || sub.getStartTime().isBefore(earliestStart)) {
                    earliestStart = sub.getStartTime();
                }
                LocalDateTime subEnd = sub.getEndTime();
                if (latestEnd == null || (subEnd != null && subEnd.isAfter(latestEnd))) {
                    latestEnd = subEnd;
                }
                totalDuration = totalDuration.plus(sub.getDuration());
            }
        }

        setStartTime(earliestStart);
        this.endTime = latestEnd;
        setDuration(totalDuration);
    }

    public List<Integer> getSubtaskIds() {
        return Collections.unmodifiableList(subtaskIds);
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtaskId(int id) {
        subtaskIds.remove((Integer) id);
    }

    public void clearSubtasks() {
        subtaskIds.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String getType() {
        return "EPIC";
    }

    @Override
    public String toString() {
        return "Epic{" + "id=" + id + ", title='" + title + '\'' + ", description='" + description + '\'' + ", \n" +
                "status=" + status + ", duration=" + duration + ", startTime=" + startTime + ", \n " +
                "endTime=" + endTime + ", subtaskIds=" + subtaskIds + '}';
    }
}