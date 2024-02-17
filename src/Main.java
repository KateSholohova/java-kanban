import main.managers.InMemoryTaskManager;
import main.managers.TaskManager;
import main.status.Status;
import main.tasks.*;


public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manage = new InMemoryTaskManager();
        manage.putTask(new Task("Первая задача", "...", Status.NEW));
        manage.putTask(new Task("Вторая задача", "!!!", Status.NEW));
        manage.putEpic(new Epic("Первый эпик", "описание"));
        manage.putSubtask(new Subtask("Первая подзадача", " ", Status.NEW, 3));
        manage.putSubtask(new Subtask("Вторая подзадача", " ", Status.NEW, 3));
        System.out.println("Вывести все задачи");
        System.out.println(manage.getTasks());
        System.out.println(manage.getEpic());
        System.out.println(manage.getSubtask());
        manage.updateTask(new Task("Измененная первая задача", " ", Status.DONE, 1));
        manage.updateEpic(new Epic("Измененный первый эпик", " ", 3));
        manage.updateSubtask(new Subtask("Измененная первая подзадача", " ", Status.DONE, 3, 4 ));
        manage.updateSubtask(new Subtask("Измененная вторая подзадача", " ", Status.IN_PROGRESS, 3, 5));
        System.out.println("Вывести изменения");
        System.out.println(manage.getTaskById(1));
        System.out.println(manage.getEpicById(3));
        System.out.println(manage.getAllEpicSubtask(3));
        System.out.println(manage.getSubtaskById(5));
        System.out.println("Выввести удаления");
        manage.delTaskById(1);
        manage.delSubtaskById(4);
        System.out.println(manage.getTasks());
        System.out.println(manage.getEpic());
        System.out.println(manage.getSubtask());



    }

}
