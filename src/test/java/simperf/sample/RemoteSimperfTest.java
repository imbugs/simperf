package simperf.sample;

import simperf.Simperf;
import simperf.remote.RemoteSimperf;
import simperf.sample.thread.MessageSender;
import simperf.sample.thread.SendMessageThread;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

public class RemoteSimperfTest {
    static MessageSender sender = new MessageSender();
    static String        server = "localhost";

    public static void main(String[] args) {
        Simperf simperf = new Simperf(10, 1000);
        simperf.setThreadFactory(new SimperfThreadFactory() {
            public SimperfThread newThread() {
                SendMessageThread t = new SendMessageThread();
                t.setSender(sender);
                return t;
            }
        });
        //simperf.start();
        RemoteSimperf remoteSimperf = new RemoteSimperf(simperf, server);
        remoteSimperf.setSession("hello kitty");
        remoteSimperf.start();
    }
}
