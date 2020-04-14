package edu.uic.cs494.a3;

public abstract class Result<T> {
    private boolean ready = false; ///AtomicBoolean ??
    private T result;

    /*
    *
    *       CompareAndSwap => infinite
    *       Atomic ref also work
    *
    *
    *       Lock-Free OR Wait-Free
    *
    * */
    public boolean isReady() {
        return ready;
    }

    protected final T get() {
        if (!this.ready)
            throw new IllegalStateException("Result is not ready");

        return result;
    }

    protected final T set(T result) {
        if (this.ready)
            throw new IllegalStateException("Result is already ready");

        this.result = result;
        this.ready = true;

        return result;
    }

    public abstract void setResult(T result);

    public abstract T getResult();
}
