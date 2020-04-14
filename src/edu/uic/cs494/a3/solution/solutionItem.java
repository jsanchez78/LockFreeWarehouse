package edu.uic.cs494.a3.solution;

import edu.uic.cs494.a3.Item;

public class solutionItem implements Item {
    String description;

    /*No locks are needed on the items
     *
     * Volatile boolean flag ensures:
     *
     *   Only one thread can access the flag at a time
     *   Changes by one thread are visible to another thread
     *
     * */

    volatile boolean flag;

    solutionItem(String description){
        this.description = description;
        this.flag = false;
        //Only true when added to a shelf

    }
    @Override
    public String toString() {
        return "ItemSolution{" + this.description + "}";
    }

    public void setFlag(Boolean flag){
        this.flag = flag;
    }

    public boolean getFlag(){
        return this.flag;
    }
}
