package edu.uic.cs494.a3.solution;

import edu.uic.cs494.a3.Result;

public class solutionResult<T> extends Result<T> {


    //18:52 ------> Bad implementation
    @Override
    public void setResult(T result) {
        super.set(result);
    }

    @Override
    public T getResult() {
        //1. Waste CPU
        //2.Data race

        //while (!this.isReady());
            synchronized (this){
                try {
                    while(!this.isReady()){
                        this.wait(1000);
                        continue;
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        return super.get();
    }


}
