package simperf.sample;

import simperf.Simperf;
import simperf.command.SimperfCommand;
import simperf.remote.RemoteSimperf;
import simperf.sample.thread.MessageSender;
import simperf.sample.thread.SendMessageThread;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

public class RemoteSimperfCommandTest {
    static MessageSender sender = new MessageSender();
    static String        server = "localhost";

    public static void main(String[] args) {
        String[] xx = new String[] { "-c", "1000", "-t", "10", "-i", "1000", "-o", "20000", "-h", "localhost", "-s", "session-hello" };
        SimperfCommand simCommand = new SimperfCommand(xx);
        simCommand.getOptions().addOption("h", "host", true, "[*] remote simperf server");
        simCommand.getOptions().addOption("s", "session", true, "[*] simperf client session");
        Simperf simperf = simCommand.create();
        if (simperf == null) {
            System.exit(-1);
        }
        simperf.setThreadFactory(new SimperfThreadFactory() {
            public SimperfThread newThread() {
                SendMessageThread t = new SendMessageThread();
                t.setSender(sender);
                return t;
            }
        });
        
        RemoteSimperf remoteSimperf = new RemoteSimperf(simperf, server);
        remoteSimperf.setSession("hello kitty");
        remoteSimperf.start();

        simperf.start();
    }
}
