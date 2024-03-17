package tests;

import org.junit.jupiter.api.BeforeEach;
import main.managers.*;
import main.status.Status;
import main.tasks.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    @Test
    public void TestAdd(){
        Managers managers = new Managers();
        TaskManager manage = managers.getDefault();
        manage.putTask(new Task("Первая задача", "...", Status.NEW));
        manage.getTaskById(1);
        Task task = new Task("Измененная первая задача", " ", Status.DONE, 1);
        manage.updateTask(task);
        manage.getTaskById(1);
        ArrayList<Task> history2 = manage.getHistory();
        assertEquals(history2.get(0), task);
    }

    @Test
    public void TestRemove(){
        Managers managers = new Managers();
        TaskManager manage = managers.getDefault();
        Task task = new Task("Первая задача", "...", Status.NEW);
        manage.putTask(task);
        manage.getTaskById(1);
        manage.putTask(new Task("Вторая задача", "!!!", Status.NEW));
        manage.getTaskById(2);
        manage.delTaskById(2);

        ArrayList<Task> history2 = manage.getHistory();
        assertEquals(history2.size(), 1);
        assertEquals(history2.get(0), task);

    }
}