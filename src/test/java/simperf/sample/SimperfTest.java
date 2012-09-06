package simperf.sample;

import simperf.Simperf;
import simperf.result.DefaultSqlFileWriter;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

public class SimperfTest {
    static MessageSender sender = new MessageSender();

    /**
     * @param args
     */
    public static void main(String[] args) {

        Simperf perf = new Simperf(10, 10);
        perf.setMaxTps(5);
        perf.getMonitorThread().setLogFile("xxx.log");
        perf.getMonitorThread().clearCallback();
        perf.getMonitorThread().registerCallback(new DefaultSqlFileWriter("xxx.sql"));
        // 打印JTL日志，会有一些性能损耗
        //JTLResult jtl = new JTLResult(perf.getMonitorThread());
        //SimperfConfig.setConfig(SimperfConfig.JTL_RESULT, jtl);

        perf.start(new SimperfThreadFactory() {
            public SimperfThread newThread() {
                SendMessageThread t = new SendMessageThread();
                t.setSender(sender);
                return t;
            }
        });
    }
}
