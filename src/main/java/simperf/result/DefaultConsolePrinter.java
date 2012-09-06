package simperf.result;

import java.util.List;

import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;

/**
 * 将结果写到控制台的默认实现
 * @author imbugs
 */
public class DefaultConsolePrinter extends DefaultCallback {
    public void onStart(MonitorThread monitorThread) {
        List<String> messages = monitorThread.getMessages();
        for (String string : messages) {
            System.out.print(string);
        }
    }

    public void onMonitor(MonitorThread monitorThread, StatInfo statInfo) {
        System.out.print(statInfo);
    }
}
