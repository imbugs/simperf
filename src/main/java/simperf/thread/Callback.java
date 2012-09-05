package simperf.thread;

import simperf.result.StatInfo;

/**
 * 回调接口
 * @author imbugs
 */
public interface Callback {
    public void onStart(MonitorThread monitorThread);

    public void onMonitor(MonitorThread monitorThread, StatInfo statInfo);

    public void onExit(MonitorThread monitorThread);
}
