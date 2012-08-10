package simperf.sample;

import simperf.Simperf;
import simperf.command.SimperfCommand;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

public class SimperfCommandTest {
    static MessageSender sender = new MessageSender();

    public static void main(String[] args) {
        String[] xx = new String[] { "-c", "10", "-t", "100", "-i", "200" };
        SimperfCommand simCommand = new SimperfCommand(xx);
        Simperf perf = simCommand.create();
        perf.start(new SimperfThreadFactory() {
            public SimperfThread newThread() {
                SendMessageThread t = new SendMessageThread();
                t.setSender(sender);
                return t;
            }
        });
    }
}
