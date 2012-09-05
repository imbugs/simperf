package simperf.thread;

import java.util.List;

import simperf.result.StatInfo;

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
