package simperf.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.Simperf;
import simperf.controllers.TimeSteppingThreadTrigger;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

public class SteppingThreadTest {
    private static final Logger logger = LoggerFactory.getLogger(SteppingThreadTest.class);

    static MessageSender        sender = new MessageSender();

    /**
     * @param args
     */
    public static void main(String[] args) {

        Simperf perf = new Simperf(10, 15);
        TimeSteppingThreadTrigger trigger = new TimeSteppingThreadTrigger(3000, 3);
        trigger.setMaxThreads(30);
        trigger.startWork(perf);
        perf.getMonitorThread().setLogFile("xxx.log");
        perf.start(new SimperfThreadFactory() {
            public SimperfThread newThread() {
                SendMessageThread t = new SendMessageThread();
                t.setSender(sender);
                return t;
            }
        });
    }

    public static void thread(Simperf perf, int size) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
        perf.thread(size);
    }
}
