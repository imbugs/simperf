package simperf.thread;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import simperf.config.Constant;
import simperf.result.DataStatistics;
import simperf.result.StatInfo;
import simperf.util.SimperfUtil;

/**
 * 打印统计线程
 * @author imbugs
 */
public class MonitorThread extends Thread {
    // simperf执行线程
    private SimperfThread[] threads;
    // 线程池
    private ExecutorService threadPool;
    // 监控周期
    private int             interval;
    // 最早一次发送的时间
    private long            earlyTime  = 0;
    // 最后一次发送时间
    private long            endTime    = 0;
    // 上一次记录
    private DataStatistics  lastData   = new DataStatistics();

    private String          logFile    = Constant.DEFAULT_RESULT_LOG;

    /**
     * 回调函数
     */
    private List<Callback>  callbacks  = new ArrayList<Callback>();

    /**
     * 一些消息
     */
    private List<String>    messages   = new ArrayList<String>();

    public MonitorThread(SimperfThread[] threads, ExecutorService threadPool, int interval) {
        this.threads = threads;
        this.threadPool = threadPool;
        this.interval = interval;
    }

    /**
     * run之前进行默认的初始化设置
     */
    protected void doInit() {
        this.registerCallback(new DefaultConsolePrinter());
        this.registerCallback(new DefaultLogFileWriter(logFile));
    }

    public void run() {
        doInit();
        doStart();
        do {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doMonitor();
        } while (!threadPool.isTerminated());
        doExit();
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
        // 获取当前统计数据
        if (earlyTime <= 0 && threads.length > 0) {
            earlyTime = threads[0].getStatistics().startTime;
            for (int i = 1; i < threads.length; i++) {
                long t = threads[i].getStatistics().startTime;
                earlyTime = earlyTime > t ? t : earlyTime;
            }
        }
        DataStatistics allCalc = new DataStatistics();
        for (int i = 0; i < threads.length; i++) {
            DataStatistics data = threads[i].getStatistics();
            allCalc.failCount += data.failCount;
            allCalc.successCount += data.successCount;
            endTime = endTime > data.endTime ? endTime : data.endTime;
        }
        // 计算统计信息
        statInfo.count = allCalc.failCount + allCalc.successCount;
        statInfo.fail = allCalc.failCount;
        statInfo.duration = endTime - earlyTime;
        statInfo.avgTps = SimperfUtil.divide(statInfo.count * 1000, statInfo.duration);

        statInfo.time = System.currentTimeMillis();

        // 统计实时信息，距离上一次统计的信息
        if (lastData.endTime != 0) {
            statInfo.tDuration = endTime - lastData.endTime;
            statInfo.tCount = statInfo.count - lastData.successCount - lastData.failCount;
            statInfo.tFail = allCalc.failCount - lastData.failCount;
            statInfo.tTps = SimperfUtil.divide(statInfo.tCount * 1000, statInfo.tDuration);
        } else {
            // 第一次统计，没有上次记录结果
            statInfo.tCount = statInfo.count;
            statInfo.tFail = statInfo.fail;
            statInfo.tDuration = statInfo.duration;
            statInfo.tTps = statInfo.avgTps;
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

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
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
