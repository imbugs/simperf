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
        logger.debug("测试步进调整线程");
        Simperf perf = new Simperf(10, 15);
        TimeSteppingThreadTrigger trigger = new TimeSteppingThreadTrigger(3000, 3);
        trigger.setMaxThreads(18);
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
}
