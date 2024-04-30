package main.managers;

import main.status.Status;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {
    File fileToSve;
    int maxId = 0;

    InMemoryHistoryManager inMemoryHistoryManager;

    public FileBackedTaskManager(String prefix, String suffix) {
        try {
            fileToSve = File.createTempFile(prefix, suffix);
            //fileToSve = new File(prefix, suffix);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileToSve.toString(), StandardCharsets.UTF_8))) {
            bufferedWriter.write("id,type,name,status,description,startTime,duration,endTime,epic \n");
            for (Task task : getTasks()) {
                bufferedWriter.write(toString(task) + "\n");
            }
            for (Epic epic : getEpic()) {
                bufferedWriter.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getSubtask()) {
                bufferedWriter.write(toString(subtask) + "\n");
            }
            if (inMemoryHistoryManager != null) {
                bufferedWriter.write(historyToString(inMemoryHistoryManager));
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в сохранение");
        }


    }

    public String toString(Task task) {

        if (task.getClass() == Subtask.class) {
            return task.getId() + "," + task.getClass().toString().substring(17) + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + task.getStartTime() + "," + task.getDuration() + "," + task.getEndTime() + "," + ((Subtask) task).getEpicId();
        } else {
            return task.getId() + "," + task.getClass().toString().substring(17) + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + task.getStartTime() + "," + task.getDuration() + "," + task.getEndTime();
        }
    }

    public void loadFromFile(File file) {

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                String line = br.readLine();
                Task someTask = fromString(line);

                if (someTask != null) {
                    if (someTask.getClass() == Subtask.class) {
                        putSubtaskFromFile((Subtask) someTask);
                        if (maxId < someTask.getId()) {
                            maxId = someTask.getId();
                        }
                    } else if (someTask.getClass() == Epic.class) {
                        putEpicFromFile((Epic) someTask);
                        if (maxId < someTask.getId()) {
                            maxId = someTask.getId();
                        }
                    } else if (someTask.getClass() == Task.class) {
                        putTaskFromFile(someTask);
                        if (maxId < someTask.getId()) {
                            maxId = someTask.getId();
                        }
                    }

                } else if (!line.contains("id")) {

                    List<Integer> hisFromFile = historyFromString(line);
                    for (int i = 0; i < hisFromFile.size(); i += 1) {
                        putHistoryFromFile(hisFromFile.get(i));
                    }
                }

            }
            findEpicSubtasks();
            setCount(maxId);
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка в загрузке");
        }
    }

    @Override
    public void putTask(Task task) {
        super.putTask(task);
        save();
    }

    @Override
    public void delAllTask() {
        super.delAllTask();
        save();
    }

    @Override
    public void delTaskById(int id) {
        super.delTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void putEpic(Epic epic) {
        super.putEpic(epic);
        save();
    }

    @Override
    public void delAllEpics() {
        super.delAllEpics();
        save();
    }

    @Override
    public void delEpicById(int id) {
        super.delEpicById(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void putSubtask(Subtask subtask) {
        super.putSubtask(subtask);
        save();
    }

    @Override
    public void delAllSubtasks() {
        super.delAllSubtasks();
        save();
    }

    @Override
    public void delSubtaskById(int id) {
        super.delSubtaskById(id);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public ArrayList<Task> getHistory() {
        inMemoryHistoryManager = history;
        save();
        return super.getHistory();
    }

    static String historyToString(HistoryManager manager) {
        ArrayList<String> hisNumber = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            hisNumber.add(Integer.toString(task.getId()));
        }
        return String.join(",", hisNumber);
    }

    public static Task fromString(String value) {
        if (value.contains("Task")) {
            String[] split = value.split(",");
            Task task = new Task(split[2], split[4], Status.valueOf(split[3]), Integer.parseInt(split[0]), LocalDateTime.parse(split[5]), Duration.parse(split[6]));
            return task;
        } else if (value.contains("Subtask")) {
            String[] split = value.split(",");
            Subtask subtask = new Subtask(split[2], split[4], Status.valueOf(split[3]), Integer.parseInt(split[8]), Integer.parseInt(split[0]), LocalDateTime.parse(split[5]), Duration.parse(split[6]));
            return subtask;
        } else if (value.contains("Epic")) {
            String[] split = value.split(",");
            Epic epic = new Epic(split[2], split[4], Integer.parseInt(split[0]));
            epic.setStatus(Status.valueOf(split[3]));
            if (!split[5].equals("null")) {
                epic.setStartTime(LocalDateTime.parse(split[5]));
                epic.setDuration(Duration.parse(split[6]));
                epic.setEndTime(LocalDateTime.parse(split[7]));
            }
            return epic;
        }
        return null;

    }

    static List<Integer> historyFromString(String value) {
        List hisFromFile = new ArrayList<>();
        String[] historyFromFile = value.split(",");
        for (int i = 0; i < historyFromFile.length; i += 1) {

            int key = Integer.parseInt(historyFromFile[i]);
            hisFromFile.add(key);
        }
        return hisFromFile;
    }


}
