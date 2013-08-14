package simperf.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.Simperf;
import simperf.config.Constant;
import simperf.result.DataStatistics;
import simperf.result.DefaultConsolePrinter;
import simperf.result.DefaultLogFileWriter;
import simperf.result.StatInfo;
import simperf.util.SimperfUtil;

/**
 * 监控统计线程
 * @author imbugs
 */
public class MonitorThread extends Thread {
    private static final Logger  logger                = LoggerFactory
                                                           .getLogger(MonitorThread.class);

    private Simperf              simperf;
    // simperf执行线程
    private List<SimperfThread>  threads;
    // 监控周期
    private int                  interval;
    // 最早一次发送的时间
    private long                 earlyTime             = 0;
    // 最后一次发送时间
    private long                 endTime               = 0;
    // 上一次记录
    private DataStatistics       lastData              = new DataStatistics();

    /**
     * 回调函数
     */
    private List<Callback>       callbacks             = new ArrayList<Callback>();

    /**
     * 一些消息
     */
    private List<String>         messages              = new ArrayList<String>();

    // 默认的控制台输出
    private DefaultCallback      defaultConsolePrinter = new DefaultConsolePrinter();
    // 默认的日志文件输出
    private DefaultLogFileWriter defaultLogFileWriter  = new DefaultLogFileWriter(
                                                           Constant.DEFAULT_RESULT_LOG);

    public MonitorThread(Simperf simperf) {
        this.simperf = simperf;
        this.threads = simperf.getThreads();
        this.interval = simperf.getInterval();

        this.registerCallback(defaultConsolePrinter);
        this.registerCallback(defaultLogFileWriter);
    }

    public void run() {
        doStart();
        do {
            SimperfUtil.sleep(interval);
            doMonitor();
        } while (!isFinish());
        try {
            this.simperf.getThreadPool().shutdown();
            this.simperf.getThreadPool().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("线程被异常打断", e);
        }
        doExit();
    }

    public boolean isFinish() {
        if (this.simperf.getThreadPool().isTerminated()) {
            return true;
        }
        boolean finish = true;
        this.simperf.getAdjustThreadLock().lock();
        int length = threads.size();
        for (int i = 0; i < length; i++) {
            if (threads.get(i).isAlive()) {
                finish = false;
                break;
            }
        }
        this.simperf.getAdjustThreadLock().unlock();
        return finish;
    }

    /**
     * 获取百分比进度，如果设置了timeout并且count=-1则返回timeout进度
     * @return 百分比进度，例：31.65
     */
    public float percentProgress() {
        this.simperf.getAdjustThreadLock().lock();
        long allTransCount = 0;
        long progressCount = 0;
        for (SimperfThread thread : threads) {
            allTransCount += thread.getTransCount();
            progressCount += thread.getCountIndex();
        }
        this.simperf.getAdjustThreadLock().unlock();

        if (allTransCount <= 0 && simperf.getTimeoutThread() != null) {
            return simperf.getTimeoutThread().percentProgress();
        } else {
            return SimperfUtil.percent(progressCount, allTransCount);
        }
    }

    public void doStart() {
        if (callbacks.size() > 0) {
            for (Callback task : callbacks) {
                task.onStart(this);
            }
        }
    }

    public void doExit() {
        if (callbacks.size() > 0) {
            for (Callback task : callbacks) {
                task.onExit(this);
            }
        }
    }

    /**
     * 获取统计数据
     * @return
     */
    public StatInfo getStatInfo() {
        StatInfo statInfo = new StatInfo();
        this.simperf.getAdjustThreadLock().lock();
        List<SimperfThread> allThreads = new ArrayList<SimperfThread>();
        allThreads.addAll(threads);
        if (simperf.getDieThreads() != null) {
            // 已经死掉的线程，统计上还需要这些数据
            allThreads.addAll(simperf.getDieThreads());
        }
        // 获取当前统计数据
        int length = allThreads.size();
        if (earlyTime <= 0 && length > 0) {
            earlyTime = allThreads.get(0).getStatistics().startTime;
            for (int i = 1; i < length; i++) {
                long t = allThreads.get(i).getStatistics().startTime;
                // min
                earlyTime = earlyTime < t ? earlyTime : t;
            }
        }
        DataStatistics allCalc = new DataStatistics();
        for (int i = 0; i < length; i++) {
            DataStatistics data = allThreads.get(i).getStatistics();
            allCalc.failCount += data.failCount;
            allCalc.successCount += data.successCount;
            allCalc.runningTime += data.runningTime;
            // max
            allCalc.maxRt = allCalc.maxRt > data.maxRt ? allCalc.maxRt : data.maxRt;
            endTime = endTime > data.endTime ? endTime : data.endTime;
            // min
            allCalc.minRt = allCalc.minRt < data.minRt ? allCalc.minRt : data.minRt;
        }
        this.simperf.getAdjustThreadLock().unlock();

        // 计算统计信息
        statInfo.count = allCalc.failCount + allCalc.successCount;
        statInfo.fail = allCalc.failCount;
        statInfo.duration = endTime - earlyTime;
        statInfo.avgTps = SimperfUtil.divide(statInfo.count * 1000, statInfo.duration);
        statInfo.maxRt = allCalc.maxRt / 1000000;
        statInfo.minRt = allCalc.minRt / 1000000;
        statInfo.runningTime = allCalc.runningTime / 1000000;
        statInfo.avgRt = SimperfUtil.divide(statInfo.runningTime , statInfo.count);
        statInfo.time = System.currentTimeMillis();

        // 统计实时信息，距离上一次统计的信息
        if (lastData.endTime != 0) {
            statInfo.tDuration = endTime - lastData.endTime;
            statInfo.tCount = statInfo.count - lastData.successCount - lastData.failCount;
            statInfo.tFail = allCalc.failCount - lastData.failCount;
            statInfo.tRunningTime = (allCalc.runningTime - lastData.runningTime) / 1000000;
            statInfo.tAvgRt = SimperfUtil.divide(statInfo.tRunningTime , statInfo.tCount);
            statInfo.tTps = SimperfUtil.divide(statInfo.tCount * 1000, statInfo.tDuration);
        } else {
            // 第一次统计，没有上次记录结果
            statInfo.tCount = statInfo.count;
            statInfo.tFail = statInfo.fail;
            statInfo.tDuration = statInfo.duration;
            statInfo.tTps = statInfo.avgTps;
            statInfo.tRunningTime = statInfo.runningTime;
        }
        // 记录上次结果，用于分析实时信息
        lastData = allCalc;
        // 上次结果中的endTime为上次发送时间
        lastData.endTime = endTime;
        return statInfo;
    }

    /**
     * 进行一次监控
     */
    public void doMonitor() {
        StatInfo statInfo = this.getStatInfo();
        if (callbacks.size() > 0) {
            for (Callback task : callbacks) {
                task.onMonitor(this, statInfo);
            }
        }
    }

    /**
     * 注册回调函数
     * @param c
     */
    public void registerCallback(Callback c) {
        this.callbacks.add(c);
    }

    /**
     * 清除回调
     * @param p
     */
    public void clearCallback() {
        this.callbacks.clear();
    }

    public void setLogFile(String logFile) {
        this.defaultLogFileWriter.setLogFile(logFile);
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void write(String message) {
        this.messages.add(message);
    }
}
