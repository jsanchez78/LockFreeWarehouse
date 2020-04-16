package edu.uic.cs494.a3.Unbounded_Lock_Free_Queue;

import java.util.concurrent.atomic.AtomicReference;

public class Lock_Free_Queue<T> {

    Node sentinel;
    AtomicReference<Node> head;
    AtomicReference<Node> tail;


    public Lock_Free_Queue(){
        sentinel = new Node();
        head = new AtomicReference<>(sentinel);
        tail = new AtomicReference<>(sentinel);
    }

    public class Node{
        public T val;
        public AtomicReference<Node> next;
        public Node(T val){
            this.val = val;
            next = new AtomicReference<>();
        }
        public Node(){
            next = new AtomicReference<>();
        }
    }
    public void enq(T val){
        Node node = new Node(val);
        while (true) {
            Node first = head.get();
            Node last = tail.get();
            Node next = last.next.get();    //NULL if not updated

            ///List at least ONE Node
            if (last == tail.get()) {
                if (next == null) {
                    if (last.next.compareAndSet(next, node)) {
                        //MAYBE issue enq to empty shelf here
                        tail.compareAndSet(last, node);
                        return;
                    }
                } else {
                    tail.compareAndSet(last, next);
                }
            }
        }
    }
    public T deq() {
        while (true) {
             Node first = head.get();
            Node last = tail.get();
            Node next = first.next.get();
            if (first == head.get()) {
                if (first == last) {
                    if (next == null) {
                        return null;
                        }
                    tail.compareAndSet(last, next);
                    } else {
                    T value = next.val;
                    if (head.compareAndSet(first, next))
                        return value;
                     }
                }
            }
        }
    public boolean isEmpty(){
        return head.get().equals(tail.get());
    }

}

