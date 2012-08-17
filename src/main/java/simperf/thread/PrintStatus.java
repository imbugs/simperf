package simperf.thread;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

import simperf.result.DataStatistics;
import simperf.util.SimperfUtil;

/**
 * 打印统计线程
 * ALL: avgTps=平均TPS ,count=发送总计数 ,duration=发送总耗时 ,fail=发送失败数
 * NOW: tTps=当前时间段TPS ,tCount=当前时间段计数 ,tDuration=当前时间段耗时 ,tFail=当前时间段失败数
 * @author imbugs
 */
public class PrintStatus extends Thread {
    private SimperfThread[]  threads;
    private ExecutorService  threadPool;

    private int              interval;
    // 最早一次发送的时间
    private long             earlyTime  = 0;
    // 最后一次发送时间
    private long             endTime    = 0;

    private FileWriter       fw         = null;
    private SimpleDateFormat sdf        = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
    // 上一次记录
    private DataStatistics   lastData   = new DataStatistics();

    private String           logFile    = "simperf-result.log";

    /**
     * 程序退出之前需要执行的代码
     */
    private List<Callback>   beforeExit = new ArrayList<Callback>();

    String                   msgFormat  = "{time:'%s' ,avgTps:%s ,count:%d ,duration:%d ,fail:%d ,tTps:%s ,tCount:%d ,tDuration:%d ,tFail:%d}\n";

    public PrintStatus(SimperfThread[] threads, ExecutorService threadPool, int interval) {
        this.threads = threads;
        this.threadPool = threadPool;
        this.interval = interval;
    }

    public void run() {
        do {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            outputMessage();
        } while (!threadPool.isTerminated());
        onExit();
    }

    public void openLogFile() {
        if (null == fw) {
            try {
                fw = new FileWriter(logFile, true);
                fw.write("======>\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onExit() {
        if (beforeExit.size() > 0) {
            for (Callback task : beforeExit) {
                task.run(this);
            }
        }
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void outputMessage() {
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

        long duration = endTime - earlyTime;
        long count = allCalc.failCount + allCalc.successCount;
        String avgTps = SimperfUtil.divide(count * 1000, duration);

        String now = sdf.format(new Date());

        String msg;

        // 统计实时信息
        if (lastData.endTime != 0) {
            long tDuration = endTime - lastData.endTime;
            long tCount = count - lastData.successCount - lastData.failCount;
            long tFail = allCalc.failCount - lastData.failCount;
            String tTps = SimperfUtil.divide(tCount * 1000, tDuration);
            msg = String.format(msgFormat, now, avgTps, count, duration, allCalc.failCount, tTps,
                tCount, tDuration, tFail);
        } else {
            // 第一次统计，没有上次记录结果
            msg = String.format(msgFormat, now, avgTps, count, duration, allCalc.failCount, avgTps,
                count, duration, allCalc.failCount);
        }

        write(msg);
        // 记录上次结果，用于分析实时信息
        lastData = allCalc;
        // 上次结果中的endTime为上次发送时间
        lastData.endTime = endTime;
    }

    public void write(String msg) {
        try {
            openLogFile();
            System.out.print(msg);
            fw.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerCallback(Callback c) {
        this.beforeExit.add(c);
    }

    public void clearCallback() {
        this.beforeExit.clear();
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

    public String getMsgFormat() {
        return msgFormat;
    }

    public void setMsgFormat(String msgFormat) {
        this.msgFormat = msgFormat;
    }
}
