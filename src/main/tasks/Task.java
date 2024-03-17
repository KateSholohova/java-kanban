package main.tasks;
import main.status.Status;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && name.equals(task.name) && description.equals(task.description)
                && status.equals(task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
    }

}



