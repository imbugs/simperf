package simperf.sample;

import simperf.Simperf;
import simperf.result.DefaultHttpWriter;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

public class SimperfHttpWriterTest {

    static MessageSender sender = new MessageSender();

    /**
     * @param args
     */
    public static void main(String[] args) {

        Simperf perf = new Simperf(10, 1000);
        perf.getMonitorThread().clearCallback();
        perf.getMonitorThread().registerCallback(new DefaultHttpWriter("http://localhost/report.php"));

        sender.sleepTime = 10;

        perf.start(new SimperfThreadFactory() {
            public SimperfThread newThread() {
                SendMessageThread t = new SendMessageThread();
                t.setSender(sender);
                return t;
            }
        });
    }

}
