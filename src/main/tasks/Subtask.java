package main.tasks;
import main.status.Status;

import java.util.Objects;

public class Subtask extends Task {
    private int EpicId;
    public Subtask(String name, String description, Status status, int EpicId) {
        super(name, description, status);
        this.EpicId = EpicId;
    }
    public Subtask(String name, String description, Status status, int EpicId, int id) {
        super(name, description, status, id);
        this.EpicId = EpicId;
    }
    @Override
    public String toString(){
        return "Название: " + getName() + "; Описание: " + getDescription() + "; id: " + getId() + "; статус: " + getStatus() + "; id эпика: " + EpicId;
    }

    public int getEpicId() {
        return EpicId;
    }

    public void setEpicId(int EpicId) {
        this.EpicId = EpicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return this.getId() == subtask.getId() && this.getName().equals(subtask.getName()) && this.getDescription().equals(subtask.getDescription())
                && this.getStatus().equals(subtask.getStatus())  && this.getEpicId()==(subtask.getEpicId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getDescription(), this.getStatus(), this.getName(), this.getEpicId());
    }

}
