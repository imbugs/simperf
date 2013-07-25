package simperf.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.Simperf;
import simperf.util.SimperfUtil;

/**
 * 超时中止线程
 * @author imbugs
 */
public class TimeoutAbortThread extends ControllThread {
    private static final Logger logger    = LoggerFactory.getLogger(TimeoutAbortThread.class);

    /**
     * 超时时间，如果为-1则表示不进行超时设置
     */
    private long                timeout   = -1;
    // 检查间隔 milliseconds
    private long                interval  = 250;
    private long                startTime = -1;

    public TimeoutAbortThread(long timeout) {
        this.timeout = timeout;
    }

    public TimeoutAbortThread(Simperf simperf, long timeout) {
        super(simperf);
        this.timeout = timeout;
    }

    public void run() {
        if (null == simperf) {
            logger.error("没有设置simperf，超时控制线程退出");
            return;
        }
        // 等待压测线程开始执行
        try {
            simperf.getThreadLatch().await();
            startTime = System.currentTimeMillis();
            // 在monitor线程中止之前保持运行状态，不断监测时间
            while (simperf.getMonitorThread().isAlive()) {
                SimperfUtil.sleep(interval);
                // 如果监测到时间已经到了
                if (System.currentTimeMillis() - startTime >= timeout && timeout > 0) {
                    simperf.stopAll();
                }
            }
            System.out.println("TOUT EXIT");
        } catch (Throwable e) {
        }
    }

    /**
     * 获取百分比进度，则返回timeout进度
     */
    public float percentProgress() {
        return SimperfUtil.percent(System.currentTimeMillis() - startTime, timeout);
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

}
