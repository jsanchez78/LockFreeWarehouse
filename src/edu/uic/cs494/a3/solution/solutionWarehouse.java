package edu.uic.cs494.a3.solution;

import edu.uic.cs494.a3.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class solutionWarehouse implements Warehouse<solutionShelf,solutionItem> {




    LinkedList<solutionShelf> shelves = new LinkedList<>();//Keep track of all the shelves

    Lock l = new ReentrantLock();     //Lock to limit concurrency

    private static final int MAX_DELAY = 2000;


    @Override
    public solutionShelf createShelf(int size) {
        try {
            l.lock();
            solutionShelf new_shelf = new solutionShelf(size);
            shelves.add(new_shelf);
            return new_shelf;
        }
        finally {
            l.unlock();
        }
    }

    @Override
    public solutionItem createItem(String description) {
        return new solutionItem(description);
    }


    @Override
    public boolean addItems(solutionShelf solutionShelf, Set<solutionItem> items) {
        solutionResult<Boolean> result = new solutionResult<>();
        Action toPerform = new Action(Action.Operation.ADD,items,result);
        solutionShelf.doAction(toPerform);
        //Blocked until result is ready
        return result.getResult();  //WAITING to unleash thread
    }

    @Override
    public boolean removeItems(solutionShelf solutionShelf, Set<solutionItem> items) {
        solutionResult<Boolean> result = new solutionResult<>();
        Action toPerform = new Action(Action.Operation.REMOVE,items,result);
        solutionShelf.doAction(toPerform);
        //Blocked until result is ready
        return result.getResult();  //WAITING to unleash thread
    }

    @Override
    public boolean moveItems(solutionShelf from, solutionShelf to, Set<solutionItem> items) {
     return false;
    }

    @Override
    public Set<solutionItem> getContents() {
        /* Gets all items inside the warehouse */
        Set<solutionItem> ret = new HashSet<>();
        for(solutionShelf s:shelves){
            ret.addAll(s.getContents());
        }
        return ret;
    }

    @Override
    public Set<solutionItem> getContents(solutionShelf solutionShelf) {
        /*
        * Only 1 thread accessing shelf contents
        *
        * No need to synchronize
        *
        * */
        solutionResult<Set<solutionItem>> result = new solutionResult<>();
        Action toPerform = new Action(Action.Operation.CONTENTS,null,result);
        solutionShelf.doAction(toPerform);
        //Blocked until result is ready
        return result.getResult();  //WAITING to unleash thread
    }

    @Override
    public Result<Boolean> addItemsAsync(solutionShelf solutionShelf, Set<solutionItem> items) {
        solutionResult<Boolean> result = new solutionResult<>();
        Action toPerform = new Action(Action.Operation.ADD,items,result);
        solutionShelf.doAction(toPerform);
        //No waiting
        return result;
    }

    @Override
    public Result<Boolean> removeItemsAsync(solutionShelf solutionShelf, Set<solutionItem> items) {
        return null;
    }

    @Override
    public Result<Boolean> moveItemsAsync(solutionShelf from, solutionShelf to, Set<solutionItem> items) {
        return null;
    }

    @Override
    public Result<Set<solutionItem>> getContentsAsync() {
        return null;
    }

    @Override
    public Result<Set<solutionItem>> getContentsAsync(solutionShelf solutionShelf) {
        return null;
    }
}
