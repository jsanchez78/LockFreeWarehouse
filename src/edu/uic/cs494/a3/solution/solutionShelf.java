package edu.uic.cs494.a3.solution;

import edu.uic.cs494.a3.Action;
import edu.uic.cs494.a3.Result;
import edu.uic.cs494.a3.Shelf;
import edu.uic.cs494.a3.Unbounded_Lock_Free_Queue.EmptyException;
import edu.uic.cs494.a3.Unbounded_Lock_Free_Queue.Lock_Free_Queue;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
        synchronized (this){
            todo_list.enq(a);
            notify();
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
                while(true){
                    try {
                        todo = todo_list.deq();
                    }
                    catch (Exception e){
                        synchronized (this) {
                            //Catch wait exception
                            try{
                                this.wait(1000);
                            }catch (InterruptedException i ){

                            }
                        }
                        continue;
                    }
                    break;//Success
                }
        return todo;
    }

    @Override
    protected void add(Set<solutionItem> items, Result<Boolean> result) {
        //Logic to add item to this shelf
        result.setResult(true);
    }

    @Override
    protected void remove(Set<solutionItem> items, Result<Boolean> result) {

    }

    @Override
    protected void contents(Result<Set<solutionItem>> result) {

    }
}
