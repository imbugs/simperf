package simperf.thread;

import java.util.concurrent.CountDownLatch;

import simperf.config.SimperfConfig;
import simperf.result.DataStatistics;
import simperf.result.JTLRecord;
import simperf.result.JTLResult;

/**
 * 任务线程
 * @author imbugs
 */
public class SimperfThread implements Runnable {

    protected long           transCount    = 0;
    protected DataStatistics statistics    = new DataStatistics();
    protected CountDownLatch threadLatch;

    /**
     * 限速设置
     */
    protected long           maxTps        = -1;
    /**
     * 记录超限次数，用以平衡速度
     */
    protected long           overflowCount = 1;

    public void run() {
        try {
            threadLatch.countDown();
            threadLatch.await();
            beforeRunTask();
            statistics.startTime = System.currentTimeMillis();
            while (transCount > 0) {
                Object obj = beforeInvoke();
                boolean result = runTask();
                if (result) {
                    statistics.successCount++;
                } else {
                    statistics.failCount++;
                }
                transCount--;
                statistics.endTime = System.currentTimeMillis();
                if (maxTps > 0) {
                    // 休眠一定时间，达到指定TPS
                    long sleepTime = calcSleepTime();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                }
                afterInvoke(result, obj);
            }
            afterRunTask();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void afterInvoke(boolean result, Object beforeInvokeResult) {
        if (SimperfConfig.isUseConfig() && SimperfConfig.hasConfig(SimperfConfig.JTL_RESULT)) {
            JTLResult jtl = (JTLResult) SimperfConfig.getConfig(SimperfConfig.JTL_RESULT);
            long tsend = (Long) beforeInvokeResult;
            jtl.addRecord(new JTLRecord(statistics.endTime - tsend, tsend, result));
        }
    }

    protected Object beforeInvoke() {
        if (SimperfConfig.isUseConfig() && SimperfConfig.hasConfig(SimperfConfig.JTL_RESULT)) {
            return System.currentTimeMillis();
        }
        return null;
    }

    /**
     * 计算休眠时间，以达到指定maxTPS
     */
    protected long calcSleepTime() {
        if (maxTps <= 0) {
            return -1;
        }
        long allCount = statistics.successCount + statistics.failCount;
        long allTime = statistics.endTime - statistics.startTime;
        if (allCount < maxTps * allTime / 1000) {
            if (overflowCount > 1) {
                overflowCount >>= 1;
            }
            return -1;
        } else {
            overflowCount <<= 1;
            float expTime = 1000 / maxTps;
            float actTime = allTime / allCount;
            long differ = (long) (expTime - actTime);
            long sleep = differ + overflowCount;
            if (sleep <= 0) {
                return 1;
            }
            return differ + overflowCount;
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

    public long getMaxTps() {
        return maxTps;
    }

    public void setMaxTps(long maxTps) {
        this.maxTps = maxTps;
    }
}
