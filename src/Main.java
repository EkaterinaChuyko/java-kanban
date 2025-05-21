import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

public class Main {
        public static void main(String[] args) {
                TaskManager manager = new TaskManager();

                Task task1 = new Task("Прочитать мангу", "Прочитать мангу Сага о Винланде");
                manager.createTask(task1);

                Task task2 = new Task("Отработать письмо", "Письмо по клиническому согласованию");
                manager.createTask(task2);

                Epic epic1 = new Epic("Аниме", "Занятие на вечер, выбор аниме");
                manager.createEpic(epic1);

                Subtask sub1 = new Subtask("Выбрать аниме", "Выбрать жанр", epic1.getId());
                manager.createSubtask(sub1);

                Subtask sub2 = new Subtask("Прочесть отзывы", "Выбрать аниме по рейтингу", epic1.getId());
                manager.createSubtask(sub2);


                Epic epic2 = new Epic("Поездка к ветеринару", "Плановый осмотр");
                manager.createEpic(epic2);

                Subtask sub3 = new Subtask("Отвезти кота к ветеринару", "Подготовить переноску", epic2.getId());
                manager.createSubtask(sub3);


                task1.setStatus(Status.NEW);
                manager.updateTask(task1);

                task2.setStatus(Status.IN_PROGRESS);
                manager.updateTask(task2);

                sub1.setStatus(Status.NEW);
                manager.updateSubtask(sub1);

                sub2.setStatus(Status.IN_PROGRESS);
                manager.updateSubtask(sub2);

                sub3.setStatus(Status.DONE);
                manager.updateSubtask(sub3);


                System.out.println("Показать все задачи: ");
                System.out.println(manager.getAllTasks());
                System.out.println("Показать все эпики: ");
                System.out.println(manager.getAllEpics());
                System.out.println("Отобразить все подзадачи: ");
                System.out.println(manager.getAllSubtasks());


                System.out.println("Проверить статус эпика после обновления: ");
                System.out.println("Эпик №1: " + manager.getEpic(epic1.getId()));
                System.out.println("Эпик №2: " + manager.getEpic(epic2.getId()));

        }
}
