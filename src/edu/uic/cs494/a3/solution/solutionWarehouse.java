package edu.uic.cs494.a3.solution;

import edu.uic.cs494.a3.*;

import java.util.Set;

public class solutionWarehouse implements Warehouse<solutionShelf,solutionItem> {


    @Override
    public solutionShelf createShelf(int size) {
        return null;
    }

    @Override
    public solutionItem createItem(String description) {
        return null;
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
        return false;
    }

    @Override
    public boolean moveItems(solutionShelf from, solutionShelf to, Set<solutionItem> items) {
        return false;
    }

    @Override
    public Set<solutionItem> getContents() {
        return null;
    }

    @Override
    public Set<solutionItem> getContents(solutionShelf solutionShelf) {
        return null;
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
