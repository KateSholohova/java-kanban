package main.managers;

import main.status.Status;
import main.tasks.Task;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {

    @Test
    void TestSaveEmpty() {
        try {
            File file = File.createTempFile("test", "txt");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            fileBackedTaskManager.putTask(new Task("Первая задача", "...", Status.NEW));
            fileBackedTaskManager.delTaskById(1);
            Managers managers;
            TaskManager manage;
            managers = new Managers();
            manage = managers.getDefault();
            ((FileBackedTaskManager) manage).loadFromFile(file);
            assertTrue(((FileBackedTaskManager) manage).tasks.size() == 0);


        } catch (IOException e) {
            System.out.println("Ошибка создания файла");
        }
    }

    @Test
    void TestSaveSomeTasks() {

        try {
            File file = File.createTempFile("test1", "txt");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            Task task = new Task("Первая задача", "...", Status.NEW);
            fileBackedTaskManager.putTask(task);
            int i = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                while (br.ready()) {
                    i += 1;
                    String line = br.readLine();
                    if (i == 2) {
                        String[] split = line.split(",");
                        Task task1 = new Task(split[2], split[4], Status.valueOf(split[3]), Integer.parseInt(split[0]));
                        assertEquals(task1.toString(), task.toString());
                    }

                }
            } catch (IOException e) {
                System.out.println("Произошла ошибка во время чтения файла.");
            }

        } catch (IOException e) {
            System.out.println("Ошибка создания файла");
        }
    }

    @Test
    void TestLoadFromFile() {

        try {
            File file = File.createTempFile("test1", "txt");
            Task task = new Task("Первая задача", "...", Status.NEW, 1);
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.toString(), StandardCharsets.UTF_8))) {
                bufferedWriter.write("id,type,name,status,description,epic \n");
                bufferedWriter.write(task.getId() + "," + task.getClass().toString().substring(17) + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription());
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка в сохранение");
            }
            Managers managers;
            TaskManager manage;
            managers = new Managers();
            manage = managers.getDefault();
            ((FileBackedTaskManager) manage).loadFromFile(file);
            assertTrue(manage.getTaskById(1) != null);


        } catch (IOException e) {
            System.out.println("Ошибка создания файла");
        }
    }


}