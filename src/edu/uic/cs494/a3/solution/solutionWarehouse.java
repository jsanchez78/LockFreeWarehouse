package edu.uic.cs494.a3.solution;

import edu.uic.cs494.a3.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class solutionWarehouse implements Warehouse<solutionShelf,solutionItem> {




    LinkedList<solutionShelf> shelves = new LinkedList<>();//Keep track of all the shelves
    LinkedList<solutionResult> results = new LinkedList<>();
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

        /*
        *   Return result solution
        *
        *

        //FULL SHELF && Test No Room Destination
    */
        if (!removeItems(from,items))
            return false;

        while (true){
            if (addItems(to,items))
                return true;
            if (addItems(from,items))
                return false;
        }
    }
    @Override
    public Set<solutionItem> getContents() {
        /* Gets all items inside the warehouse */
        solutionResult<Set<solutionItem>> result;
        Action toPerform;
        Set<solutionItem> ret = new HashSet<>();
        for(solutionShelf s:shelves){
            result = new solutionResult<>();
            toPerform = new Action(Action.Operation.CONTENTS,null,result);
            s.doAction(toPerform);
            ret.addAll(result.getResult());
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
        solutionResult<Boolean> result = new solutionResult<>();
        Action toPerform = new Action(Action.Operation.REMOVE,items,result);
        solutionShelf.doAction(toPerform);
        //No waiting
        return result;
    }

    @Override
    public Result<Boolean> moveItemsAsync(solutionShelf from, solutionShelf to, Set<solutionItem> items) {

        solutionResult<Boolean> result = new solutionResult<>();
          /*
        *   Return result solution
        *
        *

        //FULL SHELF && Test No Room Destination
        *
        *
        * TWO Conditions

        if (!removeItems(from,items))
            return false;

        while (true){
            if (addItems(to,items))
                return true;
            if (addItems(from,items))

        }
        return new solutionResult<>(){
            @Override
            public Set<solutionItem> getResult() {


            }};

           */
          return result;
    }


    @Override
    public Result<Set<solutionItem>> getContentsAsync() {
        /* Gets all items inside the warehouse */
        LinkedList<Result<Set<solutionItem>>> results = new LinkedList<>();
        solutionResult<Set<solutionItem>> result;
        Action toPerform;

        for(solutionShelf s:shelves){
            result = new solutionResult<>();
            results.add(result);
            toPerform = new Action(Action.Operation.CONTENTS,null,result);
            s.doAction(toPerform);
        }
        Set<solutionItem> result_async = new HashSet<>();

        return new solutionResult<>(){
            @Override
            public Set<solutionItem> getResult() {
                if (this.isReady())
                    return this.get();

                for (Result<Set<solutionItem>> r:results){
                    result_async.addAll(r.getResult());
                }
                setResult(result_async);
                return this.get();
            }
        };
    }
    @Override
    public Result<Set<solutionItem>> getContentsAsync(solutionShelf solutionShelf) {
        solutionResult<Set<solutionItem>> result = new solutionResult<>();
        Action toPerform = new Action(Action.Operation.CONTENTS,null,result);
        solutionShelf.doAction(toPerform);
        //Blocked until result is ready
       return result;
    }
}
