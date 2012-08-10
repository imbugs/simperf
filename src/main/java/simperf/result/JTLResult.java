package simperf.result;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import simperf.thread.Callback;
import simperf.thread.PrintStatus;

/**
 * 输出JTL结果
 * @author tinghe
 */
public class JTLResult extends Thread {
    private String                   fileName  = "simperf.jtl";
    private FileWriter               fw        = null;
    private BlockingQueue<JTLRecord> jtlRecord = new LinkedBlockingQueue<JTLRecord>();

    public JTLResult(String fileName, PrintStatus statusThread) {
        this.fileName = fileName;
        init(statusThread);
    }

    public JTLResult(PrintStatus statusThread) {
        init(statusThread);
    }

    public void init(PrintStatus statusThread) {
        try {
            fw = new FileWriter(fileName, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.start();
        // 注册回调函数，监控线程退出之前需要先结束本线程
        statusThread.registerCallback(new Callback() {
            public void run() {
                JTLResult.this.interrupt();
                while (JTLResult.this.isAlive()) {
                    try {
                        Thread.sleep(100);
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
            e.printStackTrace();
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
                fw.write(getTail());
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return fileName;
    }
}
