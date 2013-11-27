package simperf.sample;

import simperf.Simperf;
import simperf.client.ClientLatch;
import simperf.sample.thread.MessageSender;
import simperf.sample.thread.SendMessageClientThread;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

public class SimperfClientLatchTest {
    static MessageSender sender      = new MessageSender();
    static ClientLatch   clientLatch = new ClientLatch("localhost");

    /**
     * @param args
     */
    public static void main(String[] args) {

        Simperf perf = new Simperf(3, 1000);
        sender.sleepTime = 100;

        perf.start(new SimperfThreadFactory() {
            public SimperfThread newThread() {
                SendMessageClientThread t = new SendMessageClientThread(clientLatch);
                t.setSender(sender);
                return t;
            }
        });
    }
}
