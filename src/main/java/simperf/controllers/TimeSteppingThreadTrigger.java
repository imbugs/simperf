package simperf.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.Simperf;
import simperf.util.SimperfUtil;

/**
 * 定时触发线程调整
 * @author imbugs
 */
public class TimeSteppingThreadTrigger extends Thread implements SteppingThreadTrigger {
    private static final Logger logger          = LoggerFactory
                                                    .getLogger(TimeSteppingThreadTrigger.class);

    // 默认1分钟触发一次
    private int                 triggerInterval = 60 * 1000;
    // 每次变化的线程数
    private int                 step            = 10;
    // 最大线程数
    private int                 maxThreads      = -1;
    private Simperf             simperf;

    public TimeSteppingThreadTrigger(int triggerInterval, int step) {
        this.triggerInterval = triggerInterval;
        this.step = step;
    }

    public void run() {
        while (!simperf.getMonitorThread().isFinish()) {
            try {
                SimperfUtil.sleep(triggerInterval);
                trigger();
            } catch (Exception e) {
                logger.error("调整线程并发量发生异常", e);
            }
        }
    }

    public void startWork(Simperf simperf) {
        this.simperf = simperf;
        this.start();
    }

    public void trigger() {
        int currentThread = simperf.getThreadPoolSize();
        if (maxThreads > 0 && currentThread >= maxThreads) {
            return;
        }
        int adjustTo = currentThread + step;
        if (maxThreads > 0 && adjustTo > maxThreads) {
            adjustTo = maxThreads;
        }
        if (adjustTo <= 0) {
            return;
        }
        simperf.thread(adjustTo);
    }

    public int getTriggerInterval() {
        return triggerInterval;
    }

    public void setTriggerInterval(int triggerInterval) {
        this.triggerInterval = triggerInterval;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public Simperf getSimperf() {
        return simperf;
    }

    public void setSimperf(Simperf simperf) {
        this.simperf = simperf;
    }
}
