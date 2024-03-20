package main.managers;

import main.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class InMemoryHistoryManager implements HistoryManager {

    DoublyLinkedList tasksNotRepeat = new DoublyLinkedList();
    HashMap<Integer, Node> history = new HashMap<>();

    @Override
    public void add(Task task) {
        tasksNotRepeat.removeNode(history.remove(task.getId()));
        tasksNotRepeat.linkLast(task);
        history.put(task.getId(), tasksNotRepeat.tail);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return tasksNotRepeat.getTasks();
    }

    @Override
    public void remove(int id) {
        tasksNotRepeat.removeNode(history.get(id));
        history.remove(id);

    }

    class DoublyLinkedList {
        public Node<Task> head;
        public Node<Task> tail;


        public void linkLast(Task element) {
            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;

        }


        public ArrayList<Task> getTasks() {
            ArrayList<Task> his = new ArrayList<>();
            if (!history.isEmpty()) {
                if (head.data != null) {
                    Task task = head.data;
                    his.add(task);
                    if (head.next != null) {
                        Node<Task> node = head.next;
                        while (node != null) {
                            task = node.data;
                            his.add(task);
                            node = node.next;
                        }
                    }
                }
            }

            return his;

        }


        public void removeNode(Node temp) {

            if (temp != null) {
                if (temp == tasksNotRepeat.head) {
                    tasksNotRepeat.head = tasksNotRepeat.head.next;
                    if (tasksNotRepeat.head != null) tasksNotRepeat.head.prev = null;
                    else tasksNotRepeat.tail = null;
                } else if (temp == tasksNotRepeat.tail) {
                    tasksNotRepeat.tail = tasksNotRepeat.tail.prev;
                    if (tasksNotRepeat.tail != null) tasksNotRepeat.tail.next = null;
                    else tasksNotRepeat.head = tasksNotRepeat.tail;
                } else {
                    temp.prev.next = temp.next;
                    temp.next.prev = temp.prev;
                }


            }
        }
    }
}


