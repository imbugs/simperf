package simperf.thread;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.config.SimperfConfig;
import simperf.result.DataStatistics;
import simperf.result.JTLRecord;
import simperf.result.JTLResult;
import simperf.util.SimperfUtil;

/**
 * 任务线程
 * @author imbugs
 */
public class SimperfThread implements Runnable {
    private static final Logger logger        = LoggerFactory.getLogger(SimperfThread.class);

    /** 
     * 执行次数，-1表示永久执行
     */
    protected long              transCount    = 0;
    protected DataStatistics    statistics    = new DataStatistics();
    protected CountDownLatch    threadLatch;

    /**
     * 限速设置
     */
    protected long              maxTps        = -1;
    /**
     * 记录超限次数，用以平衡速度
     */
    protected long              overflowCount = 1;
    protected long              countIndex    = 0;
    /**
     * 只用于记录sample的开始时间
     */
    protected long              sampleStart   = 0;
    /**
     * 判断当前线程是否还存活
     */
    protected boolean           alive         = true;
    protected boolean           todie         = false;

    public void run() {
        try {
            warmUp();
            await();
            beforeRunTask();
            statistics.startTime = statistics.endTime = System.currentTimeMillis();
            while ((countIndex < transCount || transCount < 0) && !todie) {
                Object obj = beforeInvoke();
                sampleStart = System.nanoTime();
                boolean result = runTask();
                statistics.addRunningTime(System.nanoTime() - sampleStart);
                if (result) {
                    statistics.successCount++;
                } else {
                    statistics.failCount++;
                }
                countIndex++;
                statistics.endTime = System.currentTimeMillis();
                afterInvoke(result, obj);

                if (maxTps > 0) {
                    // 休眠一定时间，达到指定TPS
                    long sleepTime = calcSleepTime();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                }
            }
            afterRunTask();
        } catch (InterruptedException e) {
            logger.error("线程被异常打断", e);
        }
        alive = false;
    }

    /**
     * 等待所有线程就绪
     * @throws InterruptedException
     */
    protected void await() throws InterruptedException {
        threadLatch.countDown();
        threadLatch.await();
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
     * 线程预热，与 {@link #beforeRunTask()} 的不同点是 warmUp() 在阀门打开之前执行，会同步等待其它线程全部执行完毕
     */
    public void warmUp() {
    }

    /**
     * 执行runTask()之前调用，只执行一次，在阀门打开之后执行，线程不同步等待
     */
    public void beforeRunTask() {

    }

    /**
     * 执行runTask()之后调用，只执行一次
     */
    public void afterRunTask() {

    }

    public boolean runTask() {
        SimperfUtil.sleep(10);
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

    public long getCountIndex() {
        return countIndex;
    }

    public void setCountIndex(long countIndex) {
        this.countIndex = countIndex;
    }

    public void stop() {
        todie = true;
    }

    public boolean isAlive() {
        return alive;
    }
}
