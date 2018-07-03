package threadPool;

import java.util.concurrent.locks.Condition;

public class WorkItem {
    final Condition condition;
    private final Runnable work;
    boolean isExecuting;

    public Runnable getWork(){
        return work;
    }

    public WorkItem(Runnable work, Condition condition) {
        this.work = work;
        this.condition = condition;
    }
}