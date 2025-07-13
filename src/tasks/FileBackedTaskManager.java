package tasks;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    @Override
    public Task createTask(Task task) {
        Task t = super.createTask(task);
        save();
        return t;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask s = super.createSubtask(subtask);
        save();
        return s;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic e = super.createEpic(epic);
        save();
        return e;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    protected void save() {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
            writer.write("\n");
            writer.write(historyToString(getHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла", e);
        }
    }

    private String toString(Task task) {
        TaskType type;
        if (task instanceof Epic) {
            type = TaskType.EPIC;
        } else if (task instanceof Subtask) {
            type = TaskType.SUBTASK;
        } else {
            type = TaskType.TASK;
        }

        String epicId = "";
        if (task instanceof Subtask subtask) {
            epicId = String.valueOf(subtask.getEpicId());
        }

        return String.format("%d,%s,%s,%s,%s,%s", task.getId(), type, task.getTitle(), task.getStatus(), task.getDescription(), epicId);
    }

    private String historyToString(List<Task> history) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            sb.append(history.get(i).getId());
            if (i < history.size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    public static FileBackedTaskManager loadFromFile(File file, HistoryManager historyManager) {
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, file);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line = reader.readLine();

            while ((line = reader.readLine()) != null && !line.isBlank()) {
                Task task = manager.fromString(line);
                switch (task.getType()) {
                    case "TASK" -> manager.tasks.put(task.getId(), task);
                    case "EPIC" -> manager.epics.put(task.getId(), (Epic) task);
                    case "SUBTASK" -> {
                        Subtask subtask = (Subtask) task;
                        manager.subtasks.put(subtask.getId(), subtask);
                        Epic epic = manager.epics.get(subtask.getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(subtask.getId());
                        }
                    }
                }
                manager.nextId = Math.max(manager.nextId, task.getId() + 1);
            }

            String historyLine = reader.readLine();
            if (historyLine != null && !historyLine.isBlank()) {
                for (String id : historyLine.split(",")) {
                    if (!id.isBlank()) {
                        int taskId = Integer.parseInt(id);
                        if (manager.tasks.containsKey(taskId)) {
                            manager.historyManager.add(manager.tasks.get(taskId));
                        } else if (manager.epics.containsKey(taskId)) {
                            manager.historyManager.add(manager.epics.get(taskId));
                        } else if (manager.subtasks.containsKey(taskId)) {
                            manager.historyManager.add(manager.subtasks.get(taskId));
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла", e);
        }

        return manager;
    }

    private Task fromString(String value) {

        String[] fields = value.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String title = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK -> {
                Task task = new Task(title, description, status);
                task.setId(id);
                return task;
            }
            case EPIC -> {
                Epic epic = new Epic(title, description);
                epic.setId(id);
                return epic;
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(title, description, epicId, status);
                subtask.setId(id);
                return subtask;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}