package edu.uic.cs494.a3.solution;

import edu.uic.cs494.a3.Action;
import edu.uic.cs494.a3.Result;
import edu.uic.cs494.a3.Shelf;
import edu.uic.cs494.a3.Unbounded_Lock_Free_Queue.EmptyException;
import edu.uic.cs494.a3.Unbounded_Lock_Free_Queue.Lock_Free_Queue;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class solutionShelf extends Shelf<solutionItem> {

    int size;
    //Constructor
    solutionShelf(int size){
        this.size = size;
    }
    /*
     *   1. Waste CPU time
     *   2.Data Race
     *   3.Lost updates
     *
     * */

    //19:45 ---> bad implementation
    // QUEUE of Actions ----> Good implementation
    Lock_Free_Queue<Action> todo_list = new Lock_Free_Queue<>();


    //2.Data Race ----> doAction & getAction


    /*
        3.Lost updates

    Two doAction  can overwrite previous action

    Thus, false updates.


    */

    //Queue => warehouse
    @Override
    protected void doAction(Action a) {
        todo_list.enq(a);
        synchronized (allowedThread){
            allowedThread.notify();
        }
    }

    //2.Data Race ----> doAction & getAction
    //Dequeue => shelf class
    /*
    *
    * Remove from queue
    *
    * 1) Interrupted Exception => wait fails from allowed thread
    *
    * 2) Exception => deq fails
    *
    * */
    @Override
    protected Action getAction() {
        Action todo;
        synchronized (allowedThread) {
           while(true){
                   todo = todo_list.deq();
                   if (todo == null){//EMPTY
                       try{
                           allowedThread.wait(100);
                       }
                       catch (InterruptedException i){}
                       //continue;
                   }
                   else
                       return todo;

           }
        }

    }

    @Override
    protected void add(Set<solutionItem> items, Result<Boolean> result) {
        //Capacity of shelf
        if (items.size() + getContents().size() > this.size){
            synchronized (result){
                result.setResult(false);
                result.notifyAll();
            }
            return;
        }
        //Shelf cannot contain duplicates
        for (solutionItem i: items){
            if (getContents().contains(i)){
                synchronized (result){
                    result.setResult(false);
                    result.notifyAll();
                }
                return;
            }
        }
        this.addItems(items);
        result.setResult(true);
    }

    @Override
    protected void remove(Set<solutionItem> items, Result<Boolean> result) {
        /*Note:
         *   moveItems can be called and remove will be called within that by another thread
         * */
        //If all items are NOT in shelf CANNOT REMOVE
        if (!getContents().containsAll(items)){
            synchronized (result){
                result.setResult(false);
                result.notify();
            }
            return;
        }
        synchronized (result){
            this.removeItems(items);
            result.setResult(true);
        }

    }
    @Override
    protected void contents(Result<Set<solutionItem>> result) {
       result.setResult(getContents());
    }
}
