package simperf.sample;

import simperf.Simperf;
import simperf.command.SimperfCommand;
import simperf.config.Constant;
import simperf.remote.RemoteSimperf;
import simperf.sample.thread.MessageSender;
import simperf.sample.thread.SendMessageThread;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

public class RemoteSimperfCommandTest {
    static MessageSender sender = new MessageSender();

    public static void main(String[] args) {
        String[] xx = new String[] { "-c", "300", "-t", "10", "-i", "1000", "-o", "20000", "-h",
                "localhost", "-s", "888" };
        Constant.DEFAULT_NA = null;
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

        String server = simCommand.getCmd().getOptionValue("h");
        String session = simCommand.getCmd().getOptionValue("s");

        RemoteSimperf remoteSimperf = new RemoteSimperf(simperf, server);
        remoteSimperf.setSession(session);
        remoteSimperf.start();

        simperf.start();
    }
}
