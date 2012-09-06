package simperf.result;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;

/**
 * 将结果写到文件的默认实现
 * @author imbugs
 */
public class DefaultLogFileWriter extends DefaultCallback {
    private FileWriter fileWriter;

    public DefaultLogFileWriter(String logFile) {
        super();
        try {
            this.fileWriter = new FileWriter(logFile, true);
            this.fileWriter.write("======>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStart(MonitorThread monitorThread) {
        try {
            List<String> messages = monitorThread.getMessages();
            for (String string : messages) {
                this.fileWriter.write(string);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onMonitor(MonitorThread monitorThread, StatInfo statInfo) {
        try {
            this.fileWriter.write(statInfo.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onExit(MonitorThread monitorThread) {
        try {
            this.fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
