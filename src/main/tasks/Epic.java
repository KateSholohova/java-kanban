package main.tasks;

import java.util.ArrayList;
public class Epic extends Task{
    private ArrayList<Integer> SubtaskId = new ArrayList<>();
    public Epic(String name, String description){
        super(name, description);
    }
    public Epic(String name, String description, int id){
        super(name, description);
        setId(id);
    }

    @Override
    public String toString(){
        return "Название: " + getName() + "; Описание: " + getDescription() + "; id: " + getId() + "; статус: " + getStatus() + "; Список id подзадач: " + SubtaskId;
    }

    public ArrayList<Integer> getSubtaskId() {
        return SubtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> SubtaskId) {
        if (SubtaskId != null){
            this.SubtaskId = SubtaskId;
        } else{
            this.SubtaskId = new ArrayList<>();
        }

    }

}

