package simperf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import simperf.thread.PrintStatus;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

/**
 * Simperf 是一个简单的性能测试工具，它提供了一个多线程测试框架
 * <pre>
 * <b>Example:</b>
 *
 * Simperf perf = new Simperf(50, 2000, 1000, 
 *       new SimperfThreadFactory() {
 *            public SimperfThread newThread() {
 *                return new SimperfThread();
 *            }
 *       });
 * <i>// 设置结果输出文件，默认 simperf-result.log</i>
 * perf.getPrintThread().setLogFile("simperf.log");
 * <i>// 开始性能测试</i>
 * perf.start();
 * </pre>
 * @author imbugs
 */
public class Simperf {

    private int                  threadPoolSize = 50;
    private int                  loopCount      = 2000;
    private int                  interval       = 1000;
    private long                 maxTps         = -1;
    private SimperfThreadFactory threadFactory  = null;
    private PrintStatus          printThread    = null;

    private ExecutorService      threadPool     = null;
    private CountDownLatch       threadLatch    = null;
    private SimperfThread[]      threads        = null;
    private String               startInfo      = "to be start!";

    public Simperf() {
        initThreadPool();
    }

    public Simperf(int thread, int count) {
        this.threadPoolSize = thread;
        this.loopCount = count;
        initThreadPool();
    }

    public Simperf(int thread, int count, int interval) {
        this(thread, count);
        this.interval = interval;
    }

    public Simperf(int thread, int count, SimperfThreadFactory threadFactory) {
        this(thread, count);
        this.threadFactory = threadFactory;
    }

    public Simperf(int thread, int count, int interval, SimperfThreadFactory threadFactory) {
        this(thread, count, interval);
        this.threadFactory = threadFactory;
    }

    public void start(SimperfThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        start();
    }

    public void start() {
        for (int i = 0; i < threadPoolSize; i++) {
            if (null != threadFactory) {
                threads[i] = threadFactory.newThread();
            } else {
                threads[i] = new SimperfThread();
            }
            threads[i].setTransCount(loopCount);
            threads[i].setThreadLatch(threadLatch);
            threads[i].setMaxTps(maxTps);
            threadPool.execute(threads[i]);
        }
        threadPool.shutdown();
        startInfo = "Started! (THREAD_POOL_SIZE=" + threadPoolSize + ",LOOP_COUNT=" + loopCount
                    + ",INTERVAL=" + interval + ")";
        printThread.write(startInfo + "\n");
        printThread.start();
    }

    protected void initThreadPool() {
        threads = new SimperfThread[threadPoolSize];
        threadPool = Executors.newFixedThreadPool(threadPoolSize);
        threadLatch = new CountDownLatch(threadPoolSize);
        if (null == printThread) {
            printThread = new PrintStatus(threads, threadPool, interval);
        }
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public SimperfThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setThreadFactory(SimperfThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public PrintStatus getPrintThread() {
        return printThread;
    }

    public void setPrintThread(PrintStatus printThread) {
        this.printThread = printThread;
    }

    public CountDownLatch getThreadLatch() {
        return threadLatch;
    }

    public SimperfThread[] getThreads() {
        return threads;
    }

    public String getStartInfo() {
        return startInfo;
    }

    public long getMaxTps() {
        return maxTps;
    }

    public void setMaxTps(long maxTps) {
        this.maxTps = maxTps;
    }
}
