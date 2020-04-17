package edu.uic.cs494.a3.solution;

import edu.uic.cs494.a3.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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
        //Blocked until result is ready
        //return result;
        //solutionResult<Boolean> result = new solutionResult<>();
          /*
        *   Return result solution
        *
        *
        //FULL SHELF && Test No Room Destination
        *
        *
        * TWO Conditions
          */

       Result<Boolean> result_remove = removeItemsAsync(from,items);
       Result<Boolean> result_add = addItemsAsync(to,items);
            return new solutionResult<>(){
                public Boolean getResult() {
                    //volatile
                    Boolean add_passed = result_add.getResult();
                    Boolean remove_passed = result_remove.getResult();
                    //REMOVE PASSED && ADD PASSED
                    if (remove_passed  && add_passed){
                        setResult(true);
                        return this.get();
                    }
                    // REMOVE FAIL && ADD FAIL
                    if (!remove_passed){
                        if (!add_passed){
                            setResult(false);
                            return this.get();
                        }
                        //REMOVE FAILED && ADD PASSED
                        while(true) {
                            if (removeItems(to, items)) {
                                setResult(false);
                                return this.get();
                            }
                        }
                    }
                    //ADD FAILED
                        if(remove_passed && !add_passed) {
                            while (true) {
                                if (addItems(to, items)) {
                                    setResult(true);
                                    break;
                                }
                                //FAILED to add items => Put them back
                                if (addItems(from, items)) {
                                    setResult(false);
                                    break;
                                }
                            }
                        }
                    return this.get();
                }
            };
    }
    @Override
    public Result<Set<solutionItem>> getContentsAsync() {
        /* Gets all items inside the warehouse */
        LinkedList<Result<Set<solutionItem>>> results = new LinkedList<>();

        for(solutionShelf s:shelves){
            solutionResult<Set<solutionItem>> result = new solutionResult<>();
            Action toPerform = new Action(Action.Operation.CONTENTS,null,result);
            s.doAction(toPerform);
            results.add(result);
        }
        Set<solutionItem> result_async = new HashSet<>();
        return new solutionResult<>(){
            @Override
            public Set<solutionItem> getResult() {
                for (Result<Set<solutionItem>> r:results){
                    result_async.addAll(r.getResult());
                }
                setResult(result_async);
                //setResult(getContents());
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
