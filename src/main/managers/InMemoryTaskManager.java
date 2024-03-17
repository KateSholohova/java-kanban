package main.managers;

import main.status.Status;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class InMemoryTaskManager implements TaskManager {
    int count = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private InMemoryHistoryManager history = Managers.getDefaultHistory();

    public int identify() {
        return ++count;
    }

    public void putTask(Task task) {
        int id = identify();
        task.setId(id);
        tasks.put(id, task);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void delAllTask() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        history.add(task);
        return task;
    }

    public void delTaskById(int id) {
        tasks.remove(id);
        history.remove(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void putEpic(Epic epic) {
        int id = identify();
        epic.setId(id);
        epic.setStatus(Status.NEW);

        epics.put(id, epic);

    }

    public ArrayList<Epic> getEpic() {
        return new ArrayList<>(epics.values());
    }

    public void delAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            history.add(epic);
        }

        return epic;
    }

    public void delEpicById(int id) {
        ArrayList<Integer> subId = epics.get(id).getSubtaskId();
        epics.remove(id);
        for (int i = 0; i < subId.size(); i++) {
            subtasks.remove(subId.get(i));
        }
        history.remove(id);
    }

    public ArrayList<Subtask> getAllEpicSubtask(int id) {
        ArrayList<Integer> subId = epics.get(id).getSubtaskId();
        ArrayList<Subtask> copySubId = new ArrayList<>();
        for (int i = 0; i < subId.size(); i++) {
            copySubId.add(subtasks.get(subId.get(i)));
        }
        return copySubId;
    }

    public void updateEpic(Epic epic) {
        Epic oldepic = epics.get(epic.getId());
        epic.setSubtaskId(oldepic.getSubtaskId());
        epic.setStatus(oldepic.getStatus());
        epics.put(epic.getId(), epic);
    }

    public void putSubtask(Subtask subtask) {
        int id = identify();
        subtask.setId(id);
        subtasks.put(id, subtask);
        epics.get(subtask.getEpicId()).getSubtaskId().add(subtask.getId());
        calculateStatus(subtask);
    }

    public ArrayList<Subtask> getSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    public void delAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
            epic.getSubtaskId().clear();
        }

    }

    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            history.add(subtask);
        }

        return subtask;
    }


    public void delSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) return;
        epics.get(subtask.getEpicId()).getSubtaskId().remove(Integer.valueOf(id));
        subtasks.remove(id);
        calculateStatus(subtask);
        history.remove(id);

    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        calculateStatus(subtask);
    }

    private void calculateStatus(Subtask subtask) {
        Epic newEpic = epics.get(subtask.getEpicId());
        ArrayList<Integer> newSubId = newEpic.getSubtaskId();
        int countDone = 0;
        int countNew = 0;
        int countInProgress = 0;
        for (int i = 0; i < newSubId.size(); i++) {
            if (subtasks.get(newSubId.get(i)).getStatus() == Status.DONE) {
                countDone++;
            } else if (subtasks.get(newSubId.get(i)).getStatus() == Status.NEW) {
                countNew++;
            } else {
                countInProgress++;
            }
        }
        if (countDone == newSubId.size()) {
            epics.get(subtask.getEpicId()).setStatus(Status.DONE);
        } else if (countNew == newSubId.size()) {
            epics.get(subtask.getEpicId()).setStatus(Status.NEW);
        } else {
            epics.get(subtask.getEpicId()).setStatus(Status.IN_PROGRESS);
        }
    }

    public ArrayList<Task> getHistory() {
        return history.getHistory();
    }


}

