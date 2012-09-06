package simperf.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.Simperf;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

public class AdjustThreadsTest {
    private static final Logger logger = LoggerFactory.getLogger(AdjustThreadsTest.class);

    static MessageSender        sender = new MessageSender();

    /**
     * @param args
     */
    public static void main(String[] args) {

        Simperf perf = new Simperf(10, 100);
        perf.getMonitorThread().setLogFile("xxx.log");
        perf.start(new SimperfThreadFactory() {
            public SimperfThread newThread() {
                SendMessageThread t = new SendMessageThread();
                t.setSender(sender);
                return t;
            }
        });
        thread(perf, 7);
        thread(perf, 10);
        thread(perf, 15);
        thread(perf, 20);
        thread(perf, 15);
        thread(perf, 25);
        thread(perf, 10);
        thread(perf, 5);
    }

    public static void thread(Simperf perf, int size) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
        perf.thread(size);
    }
}
