package threadPool;

import model.report.ReportModel;
import org.gradle.api.logging.Logger;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FinalThreadWork {
    private final LinkedList<Runnable> work;
    private final Condition waitTermination;
    private final ReentrantLock lock;
    private final Logger logger;
    private int waitTerminationThreads;
    private boolean toTerminate;
    private ReportModel reportModel;

    public boolean addAction(Runnable action){
        if (!toTerminate) {
            work.add(action);
            return true;
        }
        return false;
    }

    /**
     * It immediately creates a worker thread that will be searching for work.
     * @param reportModel   The report model where the license found will be added.
     * @param logger    A reference to the plugin logger.
     */
    public FinalThreadWork(ReportModel reportModel, Logger logger) {
        this.logger = logger;
        this.reportModel = reportModel;
        lock = new ReentrantLock();
        waitTermination = lock.newCondition();
        waitTerminationThreads = 0;
        work = new LinkedList<>();
        toTerminate = false;

        WorkerThread workerThread = new WorkerThread();
        workerThread.start();
    }

    /**
     * Tells the thread to stop accepting jobs, and finish when all current jobs are done.
     */
    public void shutdown() {
        lock.lock();
        try {
            toTerminate = true;
            if (waitTerminationThreads > 0) {
                waitTerminationThreads = 0;
                waitTermination.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Blocks a thread waiting for the termination of the working thread
     * <br>
     * If the thread has already been order to shutdown and there are no work to do, the thread terminated successfully.
     * <br>
     * Otherwise waits until the thread finishes all the pending work.
     */
    public void awaitTermination() {
        lock.lock();
        try {

            if (work.isEmpty() && toTerminate)
                return;

            waitTerminationThreads++;
            while (true) {
                try {
                    logger.info("Wait termination.");
                    waitTermination.await(500, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    waitTerminationThreads--;
                    if (work.isEmpty() && toTerminate)
                        return;
                }
                if (work.isEmpty() && toTerminate)
                    return;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Class to represent the thread searching for work.
     */
    private class WorkerThread extends Thread {
        private Condition waitThread;
        private Runnable command;

        public WorkerThread() {
            waitThread = lock.newCondition();
        }

        @Override
        public void run() {
            logger.info("Log in WorkerThread");
            while (findWork()) {
                logger.info("Running work.");
                command.run();
            }
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

        /**
         * Search for work during its lifetime. This search has several outcomes.
         * <ol>
         *     <li>
         *         If there is already work to be done, the thread will execute it.
         *     </li>
         *     <li>
         *         If the thread was ordered to shutdown, the thread will leave.
         *     </li>
         *     <li>
         *         The thread will wait until it find work or is order to shutdown.
         *     </li>
         * </ol>
         * @return
         */
        private boolean findWork() {
            lock.lock();
            try {
                if (!work.isEmpty()){
                    logger.info("Work is not empty.");
                    command = work.removeFirst();
                    return true;
                }

                if (toTerminate) {
                    logger.info("FinalThreadWork is shutting down.");
                    return false;
                }

                while (true){
                    try {
                        logger.info("Wait thread.");
                        waitThread.await(500, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        ;//ignored
                    }
                    if (!work.isEmpty()){
                        command = work.removeFirst();
                        return true;
                    }
                    if (toTerminate) {
                        return false;
                    }
                }
            }finally {
                lock.unlock();
            }
        }
    }
}