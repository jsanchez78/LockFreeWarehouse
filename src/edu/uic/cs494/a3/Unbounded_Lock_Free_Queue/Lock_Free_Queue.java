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
        tail = head;
    }

    public class Node{
        public T val;
        public AtomicReference<Node> next;
        public Node(T val){
            this.val = val;
            next = new AtomicReference<>(null);
        }
        public Node(){
            next = new AtomicReference<>(null);
        }
    }
    public void enq(T val){
        Node node = new Node(val);
        while (true) {
            Node last = tail.get();
            Node next = last.next.get();
            if (last == tail.get()) {
                if (next == null) {
                    if (last.next.compareAndSet(null, node)) {
                        tail.compareAndSet(last, node);
                        return;
                    }
                } else {
                    tail.compareAndSet(last, next);
                }
            }
        }
    }
    public T deq() throws EmptyException {
        while (true) {
             Node first = head.get();
            Node last = tail.get();
            Node next = first.next.get();
            if (first == head.get()) {
                if (first == last) {
                    if (next == null) {
                        throw new EmptyException();
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


}

