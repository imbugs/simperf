package simperf.thread;

import java.util.concurrent.CountDownLatch;

import simperf.result.DataStatistics;

/**
 * 任务线程
 * @author imbugs
 */
public class SimperfThread implements Runnable {

    protected long           transCount = 0;
    protected DataStatistics statistics = new DataStatistics();
    protected CountDownLatch threadLatch;

    public void run() {
        try {
            threadLatch.countDown();
            threadLatch.await();
            beforeRunTask();
            statistics.startTime = System.currentTimeMillis();
            while (transCount > 0) {
                if (runTask()) {
                    statistics.successCount++;
                } else {
                    statistics.failCount++;
                }
                transCount--;
                statistics.endTime = System.currentTimeMillis();
            }
            afterRunTask();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行runTask()之前调用，只执行一次
     */
    public void beforeRunTask() {

    }

    /**
     * 执行runTask()之后调用，只执行一次
     */
    public void afterRunTask() {

    }

    public boolean runTask() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setTransCount(long transCount) {
        this.transCount = transCount;
    }

    public long getTransCount() {
        return transCount;
    }

    public void setThreadLatch(CountDownLatch threadLatch) {
        this.threadLatch = threadLatch;
    }

    public CountDownLatch getThreadLatch() {
        return threadLatch;
    }

    public DataStatistics getStatistics() {
        return statistics;
    }
}
