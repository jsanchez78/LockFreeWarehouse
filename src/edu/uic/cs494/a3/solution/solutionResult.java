package edu.uic.cs494.a3.solution;

import edu.uic.cs494.a3.Result;

public class solutionResult<T> extends Result<T> {

    solutionResult<T> waitForThis1; // This returns an int
    solutionResult<T> waitForThis2; // This returns an int
    //18:52 ------> Bad implementation
    @Override
    public void setResult(T result) {
        super.set(result);
    }

    @Override
    public T getResult() {
        //1. Waste CPU
        //2.Data race

        while (true){
            synchronized (this){
                try {
                    this.wait(1);
                    if(isReady()){
                        break;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.get();
    }


}
