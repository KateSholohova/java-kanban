package main.managers;

import java.io.File;
import java.io.IOException;

public class Managers {

    public TaskManager getDefault() {
        try {
            return new FileBackedTaskManager(File.createTempFile("test", "txt"));
        } catch (IOException e) {
            System.out.println("Ошибка создания файла");
        }
        return null;
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
