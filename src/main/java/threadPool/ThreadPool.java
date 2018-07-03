package threadPool;

import org.gradle.api.logging.Logger;
import java.util.LinkedList;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPool {
    private final ReentrantLock lock;
    private final LinkedList<WorkItem> work = new LinkedList<>();
    private final LinkedList<WorkerThread> threads = new LinkedList<>();
    private final Condition waitTermination;
    private final int maxPoolSize, keepAliveTime;
    private final Logger logger;
    private int workingThreads, waitingTerminationThreads;
    private boolean isShuttingDown;

    public ThreadPool(int maxPoolSize, int keepAliveTime, Logger logger){
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.logger = logger;
        lock = new ReentrantLock();
        waitTermination = lock.newCondition();
        waitingTerminationThreads = 0;
    }

    /**
     * During the execution of this method, there are several possibilities to how it will run:
     * <ol>
     *     <li>In case, the pool is in shutdown it will launch RejectedExecutionException.</li>
     *     <li>If there are available threads to work one is signaled to execute the command received.</li>
     *     <li>if there can be any more threads to work, one will be created to execute the command</li>
     *     <li>Create a work item, representing the command, that will be placed in the work list, waiting for a thread
     *     to become available.</li>
     * </ol>
     * @param command   Runnable to be executed in the thread
     * @throws InterruptedException When the item waiting to be executed was interrupted
     * @throws RejectedExecutionException   When the pool is shutting down
     */
    public void execute(Runnable command) throws InterruptedException, RejectedExecutionException {
        lock.lock();
        logger.info("Inside lock");
        try {
            if (isShuttingDown)
                throw new RejectedExecutionException();

            logger.info("Thread pool not shutting down.");
            if(!threads.isEmpty()){
                WorkerThread worker = threads.removeLast();
                worker.setCommand(command);
                worker.ready = true;
                worker.waitThread.signal();
                return;
            }

            if (workingThreads < maxPoolSize){
                WorkerThread worker = new WorkerThread(command);
                worker.start();
                workingThreads++;
                return;
            }

            WorkItem workItem = new WorkItem(command, lock.newCondition());
            work.add(workItem);

            while (true){
                try {
                    workItem.condition.await();
                }catch (InterruptedException e){
                    if (workItem.isExecuting) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    work.remove(workItem);
                    throw e;
                }

                if (workItem.isExecuting)
                    return;
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * The ThreadPool is ordered to shutdown and if there is already some threads waiting for the pool to shutdown,
     * those will be signaled.
     */
    public void shutdown(){
        lock.lock();
        try {
            isShuttingDown = true;
            if (waitingTerminationThreads > 0) {
                waitingTerminationThreads = 0;
                waitTermination.signalAll();
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * Blocks a thread waiting for the termination of the ThreadPool before the end of timeout.
     * <br>
     * If the pool is already shutting down with no threads busy, the pool has already terminated successfully.
     * <br>
     * Otherwise waits that all threads finish their job.
     * @param timeout   To max time to wait for the pool shutting down.
     * @return  It returns true when the pool is successfully shutdown, false otherwise.
     * @throws InterruptedException In case the thread waiting for the pool shutdown is interrupted.
     */
    public boolean awaitTermination(int timeout) throws InterruptedException{
        lock.lock();
        try {
            if (workingThreads == 0 && isShuttingDown)
                return true;

            if (Timeouts.noWait(timeout))
                return false;

            long t = Timeouts.start(timeout);
            long remaining = Timeouts.remaining(t);
            waitingTerminationThreads++;
            while(true){
                try {
                    waitTermination.await(remaining, TimeUnit.MILLISECONDS);
                }catch (InterruptedException e){
                    waitingTerminationThreads--;
                    if (workingThreads == 0 && isShuttingDown)
                        return true;
                    throw e;
                }
                if (workingThreads == 0 && isShuttingDown)
                    return true;
                remaining = Timeouts.remaining(t);
                if (Timeouts.isTimeout(remaining)){
                    waitingTerminationThreads--;
                    return false;
                }
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * Class that represents the Thread used by the ThreadPool to conduct work.
     */
    private class WorkerThread extends Thread{
        private Runnable command;
        private Condition waitThread;
        private boolean ready;
        private long timeLiving = keepAliveTime;

        public void setCommand(Runnable command){
            this.command = command;
        }

        private WorkerThread(Runnable command){
            this.command = command;
            waitThread = lock.newCondition();
            ready = true;
        }

        @Override
        public void run() {
            do{
                command.run();
            }while(findWork());
        }

            /**
             * Search for work during its lifetime. This search has several outcomes.
             * <ol>
             *     <li>
             *         If there is already work to be done, the thread will execute it and signal the work condition to
             *         leave waiting list
             *     </li>
             *     <li>
             *         If the pool is shutting down, the thread will leave, but first checks if there are no working threads,
             *         every waiting thread will be warn of the pool shutdown.
             *     </li>
             *     <li>
             *         The thread will wait for work for as long as it can keep alive.
             *     </li>
             * </ol>
             * @return  True if the thread found work, false if its leaving.
             */
        private boolean findWork() {
            lock.lock();
            try {
                if (!work.isEmpty()){
                    WorkItem current = work.removeFirst();
                    command = current.getWork();
                    current.isExecuting = true;
                    current.condition.signal();
                    return true;
                }

                if (isShuttingDown) {
                    workingThreads--;
                    if (workingThreads == 0) {
                        waitingTerminationThreads = 0;
                        waitTermination.signalAll();
                    }
                    return false;
                }

                ready = false;
                threads.add(this);
                long time = Timeouts.start(timeLiving);
                long remaining = Timeouts.remaining(time);
                while (true){
                    try {
                        waitThread.await(remaining, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        ;//ignored
                    }
                    if (ready) {
                        return true;
                    }
                    remaining = Timeouts.remaining(time);
                    if (Timeouts.isTimeout(remaining)){
                        workingThreads--;
                        threads.remove(this);
                        if (workingThreads == 0 && isShuttingDown) {
                            waitingTerminationThreads = 0;
                            waitTermination.signalAll();
                        }
                        return false;
                    }
                }
            }finally {
                lock.unlock();
            }
        }
    }
}