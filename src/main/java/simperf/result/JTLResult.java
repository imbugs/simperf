package simperf.result;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.config.Constant;
import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;

/**
 * 输出JTL结果
 * @author imbugs
 */
public class JTLResult extends Thread {
    private static final Logger      logger    = LoggerFactory.getLogger(JTLResult.class);

    private String                   fileName  = Constant.DEFAULT_JTL_FILE;
    private FileWriter               fw        = null;
    private BlockingQueue<JTLRecord> jtlRecord = new LinkedBlockingQueue<JTLRecord>();
    // 把本线程的结束回调注册到监控线程上
    private MonitorThread            statusThread;

    public JTLResult(String fileName, MonitorThread statusThread) {
        this.fileName = fileName;
        this.statusThread = statusThread;
        init();
    }

    public JTLResult(MonitorThread statusThread) {
        this.statusThread = statusThread;
        init();
    }

    public void init() {
        try {
            fw = new FileWriter(fileName, false);
        } catch (IOException e) {
            logger.error("写文件异常", e);
        }

        this.start();
        // 注册回调函数，监控线程退出之前需要先结束本线程
        this.statusThread.registerCallback(new DefaultCallback() {
            public void onExit(MonitorThread ps) {
                // 终止本线程的时候，所有threadPool中的线程已经终止了
                JTLResult.this.interrupt();
                while (JTLResult.this.isAlive()) {
                    try {
                        JTLResult.this.join();
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    public void addRecord(JTLRecord r) {
        try {
            jtlRecord.put(r);
        } catch (InterruptedException e) {
            logger.error("线程被异常打断", e);
        }
    }

    public String getHead() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<testResults version=\"1.2\">\n";
    }

    public String getRecord(JTLRecord r) {
        StringBuffer sb = new StringBuffer();
        sb.append("<sample t=\"");
        sb.append(r.elapsedTime); // 响应时间
        sb.append("\" lt=\"0\" ts=\"");
        sb.append(r.tsend); // 发送时间
        sb.append("\" s=\"");
        sb.append(r.result); // 结果标识 true/false
        sb.append("\" lb=\"Simperf Request\" rc=\"200\" rm=\"OK\" tn=\"线程组 1-");
        sb.append(r.tid); // 线程号
        sb.append("\" dt=\"text\" by=\"0\"/>\n");
        return sb.toString();
    }

    public String getTail() {
        return "\n</testResults>";
    }

    public void run() {
        try {
            fw.write(getHead());
            try {
                while (true) {
                    JTLRecord r = jtlRecord.take();
                    fw.write(getRecord(r));
                }
            } catch (InterruptedException e) {
                // 此线程在此处正常终止
                fw.write(getTail());
                fw.close();
            }
        } catch (IOException e) {
            logger.error("写文件异常", e);
        }
    }

    public String getFileName() {
        return fileName;
    }
}
