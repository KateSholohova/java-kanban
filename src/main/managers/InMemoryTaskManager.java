package main.managers;

import main.status.Status;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    private int count = 0;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();

    protected InMemoryHistoryManager history = Managers.getDefaultHistory();

    protected Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        prioritizedTasks = new TreeSet<>(taskComparator);
    }

    public int identify() {
        return ++count;
    }

    public void putTask(Task task) {
        if (!valid(task)) {
            System.out.println("Задача пересекается");
            return;
        }
        int id = identify();
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void delAllTask() {
        for (Task task : new ArrayList<>(tasks.values())) {
            prioritizedTasks.remove(task);
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
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        history.remove(id);
    }

    public void updateTask(Task task) {
        if (!valid(task)) {
            System.out.println("Задача пересекается");
            return;
        }
        tasks.put(task.getId(), task);
        Task replaceTask = tasks.get(task.getId());
        prioritizedTasks.remove(replaceTask);
        prioritizedTasks.add(task);

    }

    public void putEpic(Epic epic) {
        if (!valid(epic)) {
            System.out.println("Задача пересекается");
            return;
        }
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
            prioritizedTasks.remove(epic);
        }
        for (Subtask subtask : new ArrayList<>(subtasks.values())) {
            prioritizedTasks.remove(subtask);
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
        prioritizedTasks.remove(epics.get(id));
        epics.remove(id);
        for (int i = 0; i < subId.size(); i++) {
            prioritizedTasks.remove(subtasks.get(subId.get(i)));
            subtasks.remove(subId.get(i));
        }
        history.remove(id);
    }

    public ArrayList<Subtask> getAllEpicSubtask(int id) {
        ArrayList<Integer> subId = epics.get(id).getSubtaskId();
        ArrayList<Subtask> copySubId = new ArrayList<>();
        for (Integer integer : subId) {
            copySubId.add(subtasks.get(integer));
        }
        return copySubId;
    }

    public void updateEpic(Epic epic) {
        if (!valid(epic)) {
            System.out.println("Задача пересекается");
            return;
        }
        Epic oldepic = epics.get(epic.getId());
        epic.setSubtaskId(oldepic.getSubtaskId());
        epic.setStatus(oldepic.getStatus());
        epics.put(epic.getId(), epic);
        Epic replaceEpic = epics.get(epic.getId());
        prioritizedTasks.remove(replaceEpic);
        prioritizedTasks.add(epic);
    }

    public void putSubtask(Subtask subtask) {
        if (!valid(subtask)) {
            System.out.println("Задача пересекается");
            return;
        }
        int id = identify();
        subtask.setId(id);
        subtasks.put(id, subtask);
        epics.get(subtask.getEpicId()).getSubtaskId().add(subtask.getId());
        createEpicDateTime(epics.get(subtask.getEpicId()));
        calculateStatus(subtask);
        prioritizedTasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    public void delAllSubtasks() {
        for (Subtask subtask : new ArrayList<>(subtasks.values())) {
            prioritizedTasks.remove(subtask);
            history.remove(subtask.getId());
        }
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
            epic.getSubtaskId().clear();
            createEpicDateTime(epic);
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
        createEpicDateTime(epics.get(subtask.getEpicId()));
        history.remove(id);
        prioritizedTasks.remove(subtask);

    }

    public void updateSubtask(Subtask subtask) {
        if (!valid(subtask)) {
            System.out.println("Задача пересекается");
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        calculateStatus(subtask);
        createEpicDateTime(epics.get(subtask.getEpicId()));
        Subtask replaceSubtask = subtasks.get(subtask.getId());
        prioritizedTasks.remove(replaceSubtask);
        prioritizedTasks.add(subtask);

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
        for (Subtask subtask : subtasks.values()) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtaskId().add(subtask.getId());
            }
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

    private void createEpicDateTime(Epic epic) {
        List<Integer> subTaskList = epic.getSubtaskId();
        if (subTaskList.isEmpty()) {
            epic.setDuration(null);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }
        for (Integer subTaskId : subTaskList) {
            LocalDateTime subTaskStartTime = subtasks.get(subTaskId).getStartTime();
            LocalDateTime subTaskEndTime = subtasks.get(subTaskId).getEndTime();
            Duration subTaskDuration = subtasks.get(subTaskId).getDuration();
            if (epic.getStartTime() == null) {
                epic.setStartTime(subTaskStartTime);
                epic.setEndTime(subTaskEndTime);
                if (epic.getDuration() == null) {
                    epic.setDuration(subTaskDuration);
                } else {
                    epic.setDuration(epic.getDuration().plus(subTaskDuration));
                }
            } else {
                if (epic.getStartTime().isAfter(subTaskStartTime)) {
                    epic.setStartTime(subTaskStartTime);
                    if (epic.getDuration() == null) {
                        epic.setEndTime(subTaskEndTime);
                    } else {
                        epic.setDuration(epic.getDuration().plus(subTaskDuration));
                    }
                }
                if (subTaskEndTime.isAfter(epic.getEndTime())) {
                    epic.setEndTime(subTaskEndTime);
                    if (epic.getDuration() == null) {
                        epic.setEndTime(subTaskEndTime);
                    } else {
                        epic.setDuration(epic.getDuration().plus(subTaskDuration));
                    }
                }
            }
        }
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean valid(Task task) {
        if (prioritizedTasks.isEmpty()) {
            return true;
        }
        LocalDateTime start = task.getStartTime();
        LocalDateTime finish = task.getEndTime();
        if (start == null) {
            return true;
        }
        for (Task prioritizedTask : prioritizedTasks) {
            LocalDateTime begin = prioritizedTask.getStartTime();
            LocalDateTime end = prioritizedTask.getEndTime();
            if (start.isEqual(begin) || start.isEqual(end) || finish.isEqual(end) || finish.isEqual(begin)) {
                return false;
            }
            if ((start.isAfter(begin) && start.isBefore(end)) || (finish.isAfter(begin) && finish.isBefore(end))) {
                return false;
            }
            if (start.isBefore(begin) && finish.isAfter(end)) {
                return false;
            }
        }
        return true;
    }

    Comparator<Task> taskComparator = (o1, o2) -> {
        if (o1.getId() == o2.getId()) {
            return 0;
        }
        if (o1.getStartTime() == null) {
            return 1;
        }
        if (o2.getStartTime() == null) {
            return -1;
        }
        if (o1.getStartTime().isBefore(o2.getStartTime())) {
            return -1;
        } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
            return 1;
        } else {
            return o1.getId() - o2.getId();
        }
    };
}




