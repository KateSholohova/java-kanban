package main.managers;

import main.tasks.*;
import java.util.ArrayList;

public class Managers {

    public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
