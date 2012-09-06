package simperf.sample;

import simperf.Simperf;
import simperf.command.SimperfCommand;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

public class SimperfCommandTest {
    static MessageSender sender = new MessageSender();

    public static void main(String[] args) {
        String[] xx = new String[] { "-c", "1000", "-t", "10", "-i", "500" };
        SimperfCommand simCommand = new SimperfCommand(xx);
        Simperf perf = simCommand.create();
        if (perf == null) {
            System.exit(-1);
        }
        perf.start(new SimperfThreadFactory() {
            public SimperfThread newThread() {
                SendMessageThread t = new SendMessageThread();
                t.setSender(sender);
                return t;
            }
        });
    }
}
