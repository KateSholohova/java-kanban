package main.tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subtaskId = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id) {
        super(name, description);
        setId(id);
    }

    @Override
    public String toString() {
        return "Название: " + getName() + "; Описание: " + getDescription() + "; id: " + getId() + "; статус: " + getStatus() + "; начало выполнения: " + getStartTime() + "; продолжительность в минутах: " + getDuration() + "; Список id подзадач: " + subtaskId;
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskId) {
        if (subtaskId != null) {
            this.subtaskId = subtaskId;
        } else {
            this.subtaskId = new ArrayList<>();
        }

    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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

