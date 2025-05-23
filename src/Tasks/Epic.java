package Tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
        private final List<Integer> subtaskIds = new ArrayList<>();

        public Epic(String title, String description) {
            super(title, description);
        }

        public List<Integer> getSubtaskIds() {
            return subtaskIds;
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
        public String toString() {
            return "Эпик: " + super.toString() + " Подзадачи эпика: " + subtaskIds;
        }
    }



