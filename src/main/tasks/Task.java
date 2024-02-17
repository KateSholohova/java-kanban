package main.tasks;
import main.status.Status;
public class Task {
    private final String name;
    private final String description;
    private int id;
    private Status status;
    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;

    }
    public Task(String name, String description, Status status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public String toString() {
        return "Название: " + name + "; Описание: " + description + "; id: " + id + "; статус: " + status;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}

