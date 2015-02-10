package simperf.sample;

import java.util.ArrayList;
import java.util.List;

import simperf.Simperf;
import simperf.config.Constant;
import simperf.result.StatInfo;
import simperf.sample.thread.MessageSender;
import simperf.sample.thread.SendMessageThread;
import simperf.sample.util.StdAvg;
import simperf.thread.Callback;
import simperf.thread.MonitorThread;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

public class SimperfTest {
    static MessageSender sender = new MessageSender();

    /**
     * @param args
     */
    public static void main(String[] args) {

        Simperf perf = new Simperf(10, -1);
        perf.setMaxTps(10);
        sender.sleepTime = 100;
        perf.timeout(15000);
        perf.getMonitorThread().registerCallback(new Callback() {
            private List<Double> numbers = new ArrayList<Double>();

            public void onStart(MonitorThread monitorThread) {

            }

            public void onMonitor(MonitorThread monitorThread, StatInfo statInfo) {
                if (!Constant.DEFAULT_NA.equals(statInfo.tTps)) {
                    numbers.add(Double.valueOf(statInfo.tTps));
                }
            }

            public void onExit(MonitorThread monitorThread) {
                System.out.println("##### STD. #### " + StdAvg.getStd(numbers) + " #####");
            }
        });
        perf.start(new SimperfThreadFactory() {
            public SimperfThread newThread() {
                SendMessageThread t = new SendMessageThread();
                t.setSender(sender);
                return t;
            }
        });
    }
}
