package main.tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> SubtaskId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id) {
        super(name, description);
        setId(id);
    }

    @Override
    public String toString() {
        return "Название: " + getName() + "; Описание: " + getDescription() + "; id: " + getId() + "; статус: " + getStatus() + "; Список id подзадач: " + SubtaskId;
    }

    public ArrayList<Integer> getSubtaskId() {
        return SubtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> SubtaskId) {
        if (SubtaskId != null) {
            this.SubtaskId = SubtaskId;
        } else {
            this.SubtaskId = new ArrayList<>();
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return this.getId() == epic.getId() && this.getName().equals(epic.getName()) && this.getDescription().equals(epic.getDescription())
                && this.getStatus().equals(epic.getStatus()) && this.getSubtaskId().equals(epic.getSubtaskId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getDescription(), this.getStatus(), this.getName(), this.getSubtaskId());
    }

}

