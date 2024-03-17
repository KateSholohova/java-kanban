package main.managers;

import main.status.Status;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    private static Managers managers;
    private static TaskManager manage;
    private static Task task1;
    private static Task task2;
    private static Epic epic;
    private static Subtask subtask1;
    private static Subtask subtask2;

    @BeforeEach
    public void beforeEach() {
        managers = new Managers();
        manage = managers.getDefault();
        task1 = new Task("Первая задача", "...", Status.NEW);
        task2 = new Task("Вторая задача", "!!!", Status.NEW);
        manage.putTask(task1);
        manage.putTask(task2);
        epic = new Epic("Первый эпик", "описание");
        manage.putEpic(epic);
        subtask1 = new Subtask("Первая подзадача", " ", Status.NEW, 3);
        subtask2 = new Subtask("Вторая подзадача", " ", Status.NEW, 3);
        manage.putSubtask(subtask1);
        manage.putSubtask(subtask2);
    }

    @Test
    void TestPutTask() {
        Task Testtask = manage.getTaskById(1);
        assertNotNull(Testtask, "Задача не найдена.");
        assertEquals(Testtask, task1, "Задачи не совпадают.");
    }

    @Test
    void TestDelAllTask() {
        manage.delAllTask();
        assertTrue(0 == manage.getTasks().size());
    }

    @Test
    void TestGetTasks() {
        assertEquals(2, manage.getTasks().size());
    }

    @Test
    void TestDelTaskById() {
        Task Testtask = manage.getTaskById(1);
        manage.delTaskById(1);
        assertFalse(manage.getTasks().contains(Testtask));
    }

    @Test
    void updateTask() {
        Task Testtask = new Task("Измененная первая задача", " ", Status.DONE, 1);
        manage.updateTask(Testtask);
        assertEquals(Testtask, manage.getTaskById(1));
    }

    @Test
    void TestPutEpic() {
        Epic Testepic = manage.getEpicById(3);
        assertNotNull(Testepic, "Задача не найдена.");
        assertEquals(Testepic, epic, "Задачи не совпадают.");
    }

    @Test
    void getEpic() {
        assertEquals(1, manage.getEpic().size());
    }

    @Test
    void TestDelAllEpics() {
        manage.delAllEpics();
        assertTrue(0 == manage.getEpic().size());
    }

    @Test
    void delEpicById() {
        Epic Testepic = manage.getEpicById(3);
        manage.delEpicById(3);
        assertFalse(manage.getEpic().contains(Testepic));
    }

    @Test
    void getAllEpicSubtask() {
        assertEquals(2, manage.getAllEpicSubtask(3).size());
    }

    @Test
    void updateEpic() {
        Epic Testepic = new Epic("Измененный первый эпик", " ", 3);
        manage.updateEpic(Testepic);
        assertEquals(Testepic, manage.getEpicById(3));
    }

    @Test
    void putSubtask() {
        Subtask TestSub = manage.getSubtaskById(4);
        assertNotNull(TestSub, "Задача не найдена.");
        assertEquals(TestSub, subtask1, "Задачи не совпадают.");
    }

    @Test
    void getSubtask() {
        assertEquals(2, manage.getSubtask().size());
    }

    @Test
    void TestDelAllSubtasks() {
        manage.delAllSubtasks();
        assertTrue(0 == manage.getSubtask().size());
    }

    @Test
    void delSubtaskById() {
        Subtask TestSub = manage.getSubtaskById(4);
        manage.delSubtaskById(4);
        assertFalse(manage.getSubtask().contains(TestSub));
        assertFalse(manage.getEpicById(3).getSubtaskId().contains(TestSub.getId()));
    }

    @Test
    void updateSubtask() {
        Subtask Testsub = new Subtask("Измененная первая подзадача", " ", Status.DONE, 3, 4);
        manage.updateSubtask(Testsub);
        assertEquals(Testsub, manage.getSubtaskById(4));
    }


}