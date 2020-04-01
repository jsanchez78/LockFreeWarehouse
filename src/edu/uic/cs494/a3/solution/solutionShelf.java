package edu.uic.cs494.a3.solution;

import edu.uic.cs494.a3.Action;
import edu.uic.cs494.a3.Result;
import edu.uic.cs494.a3.Shelf;

import java.util.Set;

public class solutionShelf extends Shelf<solutionItem> {

    /*
     *   1. Waste CPU time
     *   2.Data Race
     *   3.Lost updates
     *
     * */
    //19:45 ---> bad implementation
    Action todo = null; //Assumes ONE Action -----> WILL be multiple

    //2.Data Race ----> doAction & getAction


    /*
        3.Lost updates

    Two doAction  can overwrite previous action

    Thus, false updates.


    */
    @Override
    protected void doAction(Action a) {
        todo = a;
    }

    //2.Data Race ----> doAction & getAction
    @Override
    protected Action getAction() {
        while (todo == null);//1. Waste CPU time


        Action actiontodo = todo;
        todo = null;
        return actiontodo;
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
