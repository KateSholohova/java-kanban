package main.managers;

public class Managers {

    public TaskManager getDefault() {
        return new FileBackedTaskManager("test", "txt");
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
