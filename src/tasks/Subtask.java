package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String title, String description, int epicId, Status status, Duration duration, LocalDateTime startTime) {
        super(title, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int epicId, Status status) {
        this(title, description, epicId, status, Duration.ZERO, null);
    }

    public Subtask(String title, String description, int epicId) {
        this(title, description, epicId, Status.NEW, Duration.ZERO, null);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String getType() {
        return "SUBTASK";
    }

    @Override
    public String toString() {
        return "Subtask{" + "id=" + getId() + ", title='" + getTitle() + '\'' + ", description='" + getDescription() + '\'' + ", \n " +
                "status=" + getStatus() + ", duration=" + getDuration() + ", \n " +
                "startTime=" + getStartTime() + ", endTime=" + getEndTime() + ", epicId=" + epicId + '}';
    }
}