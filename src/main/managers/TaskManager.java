package main.managers;

import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {


    void putTask(Task task);

     ArrayList<Task> getHistory();

    ArrayList<Task> getTasks();

    void delAllTask();

    Task getTaskById(int id);

    void delTaskById(int id);

    void updateTask(Task task);

    void putEpic(Epic epic);

    ArrayList<Epic> getEpic();

    void delAllEpics();

    Epic getEpicById(int id);

    void delEpicById(int id);

    ArrayList<Subtask> getAllEpicSubtask(int id);

    void updateEpic(Epic epic);

    void putSubtask(Subtask subtask);

    ArrayList<Subtask> getSubtask();

    void delAllSubtasks();

    Subtask getSubtaskById(int id);

    void delSubtaskById(int id);

    void updateSubtask(Subtask subtask);
}
