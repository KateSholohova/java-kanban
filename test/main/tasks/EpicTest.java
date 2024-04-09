package main.tasks;

import main.managers.Managers;
import main.managers.TaskManager;
import main.status.Status;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {


    @Test
    public void TestEpicStatus() {
        Managers managers = new Managers();
        TaskManager manage = managers.getDefault();
        manage.putEpic(new Epic("1", "11"));
        manage.putEpic(new Epic("2", "22"));
        manage.putEpic(new Epic("3", "33"));
        manage.putEpic(new Epic("4", "44"));
        manage.putSubtask(new Subtask(" ", " ", Status.NEW, 1, LocalDateTime.of(1, 1, 1, 1, 1, 1), Duration.ofMinutes(60)));
        manage.putSubtask(new Subtask(" ", " ", Status.NEW, 1, LocalDateTime.of(2, 1, 1, 1, 1, 1), Duration.ofMinutes(60)));
        manage.putSubtask(new Subtask(" ", " ", Status.DONE, 2, LocalDateTime.of(3, 1, 1, 1, 1, 1), Duration.ofMinutes(60)));
        manage.putSubtask(new Subtask(" ", " ", Status.DONE, 2, LocalDateTime.of(4, 1, 1, 1, 1, 1), Duration.ofMinutes(60)));
        manage.putSubtask(new Subtask(" ", " ", Status.IN_PROGRESS, 3, LocalDateTime.of(5, 1, 1, 1, 1, 1), Duration.ofMinutes(60)));
        manage.putSubtask(new Subtask(" ", " ", Status.IN_PROGRESS, 3, LocalDateTime.of(6, 1, 1, 1, 1, 1), Duration.ofMinutes(60)));
        manage.putSubtask(new Subtask(" ", " ", Status.NEW, 4, LocalDateTime.of(7, 1, 1, 1, 1, 1), Duration.ofMinutes(60)));
        manage.putSubtask(new Subtask(" ", " ", Status.DONE, 4, LocalDateTime.of(8, 1, 1, 1, 1, 1), Duration.ofMinutes(60)));
        assertEquals(manage.getEpicById(1).getStatus(), Status.NEW);
        assertEquals(manage.getEpicById(2).getStatus(), Status.DONE);
        assertEquals(manage.getEpicById(3).getStatus(), Status.IN_PROGRESS);
        assertEquals(manage.getEpicById(4).getStatus(), Status.IN_PROGRESS);


    }

}