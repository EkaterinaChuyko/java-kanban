package Tasks;

public class Main {
        public static void main(String[] args) {
                HistoryManager historyManager = new InMemoryHistoryManager();
                InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);

                Task task1 = new Task("Прочитать мангу", "Прочитать мангу Сага о Винланде");
                taskManager.createTask(task1);

                Task task2 = new Task("Отработать письмо", "Письмо по клиническому согласованию");
                taskManager.createTask(task2);

                Epic epic1 = new Epic("Аниме", "Занятие на вечер, выбор аниме");
                taskManager.createEpic(epic1);

                Subtask sub1 = new Subtask("Выбрать аниме", "Выбрать жанр", epic1.getId());
                taskManager.createSubtask(sub1);

                Subtask sub2 = new Subtask("Прочесть отзывы", "Выбрать аниме по рейтингу", epic1.getId());
                taskManager.createSubtask(sub2);


                Epic epic2 = new Epic("Поездка к ветеринару", "Плановый осмотр");
                taskManager.createEpic(epic2);

                Subtask sub3 = new Subtask("Отвезти кота к ветеринару", "Подготовить переноску", epic2.getId());
                taskManager.createSubtask(sub3);


                task1.setStatus(Status.NEW);
                taskManager.updateTask(task1);

                task2.setStatus(Status.IN_PROGRESS);
                taskManager.updateTask(task2);

                sub1.setStatus(Status.NEW);
                taskManager.updateSubtask(sub1);

                sub2.setStatus(Status.IN_PROGRESS);
                taskManager.updateSubtask(sub2);

                sub3.setStatus(Status.DONE);
                taskManager.updateSubtask(sub3);


                System.out.println("Показать все задачи: ");
                System.out.println(taskManager.getAllTasks());
                System.out.println("Показать все эпики: ");
                System.out.println(taskManager.getAllEpics());
                System.out.println("Отобразить все подзадачи: ");
                System.out.println(taskManager.getAllSubtasks());


                System.out.println("Проверить статус эпика после обновления: ");
                System.out.println("Эпик №1: " + taskManager.getEpic(epic1.getId()));
                System.out.println("Эпик №2: " + taskManager.getEpic(epic2.getId()));


                taskManager.getTask(task1.getId());
                taskManager.getEpic(epic1.getId());
                taskManager.getSubtask(sub1.getId());
                taskManager.getTask(task2.getId());
                taskManager.getTask(task1.getId());

                System.out.println("История просмотров:");
                for (Task viewed : taskManager.getHistory()) {
                        System.out.println(viewed);
                }
        }
}