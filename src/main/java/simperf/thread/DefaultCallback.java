package simperf.thread;

import simperf.result.StatInfo;

/**
 * 回调类默认实现
 * @author imbugs
 */
public class DefaultCallback implements Callback {
    public void onStart(MonitorThread monitorThread) {
    }

    public void onMonitor(MonitorThread monitorThread, StatInfo statInfo) {
    }

    public void onExit(MonitorThread monitorThread) {
    }
}
