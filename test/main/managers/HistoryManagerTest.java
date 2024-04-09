package main.managers;

import main.status.Status;
import main.tasks.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryManagerTest {

    @Test
    public void TestAdd() {
        Managers managers = new Managers();
        TaskManager manage = managers.getDefault();
        manage.putTask(new Task("Первая задача", "...", Status.NEW, LocalDateTime.of(2024, 8, 4, 8, 0), Duration.ofMinutes(60)));
        manage.getTaskById(1);
        Task task = new Task("Измененная первая задача", " ", Status.DONE, 1, LocalDateTime.of(2029, 8, 4, 8, 0), Duration.ofMinutes(60));
        manage.updateTask(task);
        manage.getTaskById(1);
        ArrayList<Task> history2 = manage.getHistory();
        assertEquals(history2.get(0), task);
    }

    @Test
    public void TestRemove() {
        Managers managers = new Managers();
        TaskManager manage = managers.getDefault();
        Task task = new Task("Первая задача", "...", Status.NEW, LocalDateTime.of(2024, 8, 4, 8, 0), Duration.ofMinutes(60));
        manage.putTask(task);
        manage.getTaskById(1);
        manage.putTask(new Task("Вторая задача", "!!!", Status.NEW, LocalDateTime.of(2024, 9, 4, 8, 0), Duration.ofMinutes(60)));
        manage.getTaskById(2);
        manage.delTaskById(2);

        ArrayList<Task> history2 = manage.getHistory();
        assertEquals(history2.size(), 1);
        assertEquals(history2.get(0), task);

    }

    @Test
    void addSomeTasksDoubleTime() {
        Managers managers = new Managers();
        TaskManager manage = managers.getDefault();
        Task task1 = new Task("1", "1", Status.NEW, LocalDateTime.of(2024, 3, 3, 3, 3), Duration.ofMinutes(456));
        manage.putTask(task1);
        manage.getTaskById(1);
        ArrayList<Task> tasksTest = manage.getHistory();
        assertEquals(1, tasksTest.size());
        assertEquals(task1, tasksTest.get(0));

    }

    @Test
    void removeFirstElement() {
        Managers managers = new Managers();
        TaskManager manage = managers.getDefault();
        Task task1 = new Task("1", "1", Status.NEW, LocalDateTime.of(2024, 3, 3, 3, 3), Duration.ofMinutes(456));
        Task task2 = new Task("1", "1", Status.NEW, LocalDateTime.of(2025, 3, 3, 3, 3), Duration.ofMinutes(456));
        Task task3 = new Task("1", "1", Status.NEW, LocalDateTime.of(2026, 3, 3, 3, 3), Duration.ofMinutes(456));
        manage.putTask(task1);
        manage.putTask(task2);
        manage.putTask(task3);
        manage.getTaskById(1);
        manage.getTaskById(2);
        manage.getTaskById(3);
        ArrayList<Task> history = manage.getHistory();
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        manage.delTaskById(1);
        ArrayList<Task> history1 = manage.getHistory();
        assertEquals(2, history1.size());
        assertEquals(task2, history1.get(0));
    }

    @Test
    void removeLastElement() {
        Managers managers = new Managers();
        TaskManager manage = managers.getDefault();
        Task task1 = new Task("1", "1", Status.NEW, LocalDateTime.of(2024, 3, 3, 3, 3), Duration.ofMinutes(456));
        Task task2 = new Task("1", "1", Status.NEW, LocalDateTime.of(2025, 3, 3, 3, 3), Duration.ofMinutes(456));
        Task task3 = new Task("1", "1", Status.NEW, LocalDateTime.of(2026, 3, 3, 3, 3), Duration.ofMinutes(456));
        manage.putTask(task1);
        manage.putTask(task2);
        manage.putTask(task3);
        manage.getTaskById(1);
        manage.getTaskById(2);
        manage.getTaskById(3);
        ArrayList<Task> history = manage.getHistory();
        assertEquals(3, history.size());
        assertEquals(task3, history.get(2));
        manage.delTaskById(3);
        ArrayList<Task> history1 = manage.getHistory();
        assertEquals(2, history1.size());
        assertEquals(task2, history1.get(1));
    }

    @Test
    void removeMiddleElement() {
        Managers managers = new Managers();
        TaskManager manage = managers.getDefault();
        Task task1 = new Task("1", "1", Status.NEW, LocalDateTime.of(2024, 3, 3, 3, 3), Duration.ofMinutes(456));
        Task task2 = new Task("1", "1", Status.NEW, LocalDateTime.of(2025, 3, 3, 3, 3), Duration.ofMinutes(456));
        Task task3 = new Task("1", "1", Status.NEW, LocalDateTime.of(2026, 3, 3, 3, 3), Duration.ofMinutes(456));
        manage.putTask(task1);
        manage.putTask(task2);
        manage.putTask(task3);
        manage.getTaskById(1);
        manage.getTaskById(2);
        manage.getTaskById(3);
        ArrayList<Task> history = manage.getHistory();
        assertEquals(3, history.size());
        assertEquals(task2, history.get(1));
        manage.delTaskById(3);
        ArrayList<Task> history1 = manage.getHistory();
        assertEquals(2, history1.size());
        assertEquals(task2, history1.get(1));
    }
}