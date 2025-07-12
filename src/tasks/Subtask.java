package tasks;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String title, String description, int epicId, Status status) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int epicId) {
        this(title, description, epicId, Status.NEW);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Подзадача: " + super.toString() + " (эпик ID: " + epicId + ")";
    }

    @Override
    public String getType() {
        return "SUBTASK";
    }
}