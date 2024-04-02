package main.managers;

import main.status.Status;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class InMemoryTaskManager implements TaskManager {
    private int count = 0;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();

    protected InMemoryHistoryManager history = Managers.getDefaultHistory();

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
        for (Task task : new ArrayList<>(tasks.values())) {
            history.remove(task.getId());
        }
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
        for (Epic epic : new ArrayList<>(epics.values())) {
            history.remove(epic.getId());
        }
        for (Subtask subtask : new ArrayList<>(subtasks.values())) {
            history.remove(subtask.getId());
        }
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
        for (Subtask subtask : new ArrayList<>(subtasks.values())) {
            history.remove(subtask.getId());
        }
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
            epic.getSubtaskId().clear();
        }
        subtasks.clear();


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

    protected void putTaskFromFile(Task task) {
        tasks.put(task.getId(), task);
    }

    protected void putEpicFromFile(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    protected void putSubtaskFromFile(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    protected void findEpicSubtasks() {
        for (Epic epic : epics.values()) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getEpicId() == epic.getId()) {
                    epic.getSubtaskId().add(subtask.getId());
                }
            }
            epics.put(epic.getId(), epic);
        }
    }

    public void setCount(int count) {
        this.count = count;
    }

    protected void putHistoryFromFile(int idOfTask) {
        if (tasks.containsKey(idOfTask)) {
            history.add(tasks.get(idOfTask));
        } else if (subtasks.containsKey(idOfTask)) {
            history.add(subtasks.get(idOfTask));
        } else if (epics.containsKey(idOfTask)) {
            history.add(epics.get(idOfTask));
        }
    }


}

