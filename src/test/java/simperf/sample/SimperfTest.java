package simperf.sample;

import simperf.Simperf;
import simperf.config.SimperfConfig;
import simperf.result.JTLResult;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

public class SimperfTest {
    static MessageSender sender = new MessageSender();

    /**
     * @param args
     */
    public static void main(String[] args) {

        Simperf perf = new Simperf(10, 100);
        perf.setMaxTps(5);
        perf.getPrintThread().setLogFile("xxx.log");

        JTLResult jtl = new JTLResult(perf.getPrintThread());
        SimperfConfig.setConfig(SimperfConfig.JTL_RESULT, jtl);

        perf.start(new SimperfThreadFactory() {
            public SimperfThread newThread() {
                SendMessageThread t = new SendMessageThread();
                t.setSender(sender);
                return t;
            }
        });
    }
}
