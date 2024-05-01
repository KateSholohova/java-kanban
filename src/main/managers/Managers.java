package main.managers;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTaskManager("test", "txt");
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
