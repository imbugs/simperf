package simperf.sample;

import simperf.Simperf;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

public class SimperfTest {
    static MessageSender sender = new MessageSender();

    /**
     * @param args
     */
    public static void main(String[] args) {
        Simperf perf = new Simperf(2, 1000000000);
        perf.setMaxTps(5);
        perf.getPrintThread().setLogFile("xxx.log");
        perf.start(new SimperfThreadFactory() {
            public SimperfThread newThread() {
                SendMessageThread t = new SendMessageThread();
                t.setSender(sender);
                return t;
            }
        });
    }
}
