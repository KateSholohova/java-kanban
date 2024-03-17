package main.managers;

import main.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;



public class InMemoryHistoryManager implements HistoryManager {

    DoublyLinkedList<Task> tasks_not_repeat = new DoublyLinkedList<>();
    HashMap<Integer, Node> history = new HashMap<>();

    @Override
    public void add(Task task) {
        if (!(history.containsKey(task.getId()))){
            tasks_not_repeat.linkLast(task);
            history.put(task.getId(), tasks_not_repeat.tail);
        } else {
            tasks_not_repeat.removeNode(history.get(task.getId()));
            history.remove(task.getId());
            tasks_not_repeat.linkLast(task);
            history.put(task.getId(), tasks_not_repeat.tail);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return tasks_not_repeat.getTasks();
    }

    @Override
    public void remove(int id){
        tasks_not_repeat.removeNode(history.get(id));
        history.remove(id);

    }
    class DoublyLinkedList<Task> {
        public Node<Task> head;
        public Node<Task> tail;
        private int size = 0;

        public void linkLast(Task element) {
            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            size++;


        }


        public ArrayList<Task> getTasks(){
            ArrayList<Task> his = new ArrayList<>();
            if(head.data != null){
                Task task = head.data;
                his.add(task);
                if(head.next != null){
                    Node<Task> node = head.next;
                    while (node != null){
                        task = node.data;
                        his.add(task);
                        node = node.next;
                    }
                }
            }

            return his;

        }
        public int size() {
            return this.size;
        }
        public void removeNode(Node temp){

            if(temp!=null){
                if(temp==tasks_not_repeat.head){

                    tasks_not_repeat.head=tasks_not_repeat.head.next;
                    if(tasks_not_repeat.head!=null) tasks_not_repeat.head.prev=null;
                    else tasks_not_repeat.tail=null;
                }
                else if(temp==tasks_not_repeat.tail){
                    tasks_not_repeat.tail=tasks_not_repeat.tail.prev;
                    if(tasks_not_repeat.tail!=null) tasks_not_repeat.tail.next=null;
                    else tasks_not_repeat.head=tasks_not_repeat.tail;

                }
                else{
                    temp.prev.next=temp.next;
                    temp.next.prev=temp.prev;

                }

            }
        }
    }
}


