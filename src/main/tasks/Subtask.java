package main.tasks;
import main.status.Status;
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

}
