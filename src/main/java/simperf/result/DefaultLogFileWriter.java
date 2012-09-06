package simperf.result;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;

/**
 * 将结果写到文件的默认实现
 * @author imbugs
 */
public class DefaultLogFileWriter extends DefaultCallback {
    private static final Logger logger = LoggerFactory.getLogger(DefaultLogFileWriter.class);

    private FileWriter          fileWriter;
    private String              logFile;

    public DefaultLogFileWriter(String logFile) {
        super();
        this.logFile = logFile;
    }

    public void onStart(MonitorThread monitorThread) {
        try {
            this.fileWriter = new FileWriter(logFile, true);
            this.fileWriter.write("======> " + System.currentTimeMillis() + "\n");
        } catch (IOException e) {
            logger.error("写文件异常", e);
        }
        try {
            List<String> messages = monitorThread.getMessages();
            for (String string : messages) {
                this.fileWriter.write(string + "\n");
            }
        } catch (IOException e) {
            logger.error("写文件异常", e);
        }
    }

    public void onMonitor(MonitorThread monitorThread, StatInfo statInfo) {
        try {
            this.fileWriter.write(statInfo.toString() + "\n");
        } catch (IOException e) {
            logger.error("写文件异常", e);
        }
    }

    public void onExit(MonitorThread monitorThread) {
        try {
            this.fileWriter.close();
        } catch (IOException e) {
            logger.error("写文件异常", e);
        }
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }
}
