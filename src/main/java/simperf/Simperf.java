package simperf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.thread.MonitorThread;
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
 * perf.getMonitorThread().setLogFile("simperf.log");
 * <i>// 开始性能测试</i>
 * perf.start();
 * </pre>
 * @author imbugs
 */
public class Simperf {
    private static final Logger  logger           = LoggerFactory.getLogger(Simperf.class);

    private int                  threadPoolSize   = 50;
    private int                  loopCount        = 2000;
    private int                  interval         = 1000;
    private long                 maxTps           = -1;
    private SimperfThreadFactory threadFactory    = null;
    private MonitorThread        monitorThread    = null;

    /**
     * 执行线程池，线程池初始化为设置的threadPoolSize，线程池不能主动关闭，否则无法添加新线程
     */
    private ExecutorService      threadPool       = null;
    private CountDownLatch       threadLatch      = null;
    private List<SimperfThread>  threads          = new ArrayList<SimperfThread>();
    private String               startInfo        = "{}";
    /**
     * JSON Style infomation
     */
    private String               extInfo          = null;
    private ReentrantLock        adjustThreadLock = new ReentrantLock();
    // 将要中止掉的线程
    private List<SimperfThread>  dieThreads       = new ArrayList<SimperfThread>();

    public Simperf() {
        initThreadPool();
    }

    public Simperf(int thread, int count) {
        this.threadPoolSize = thread;
        this.loopCount = count;
        initThreadPool();
    }

    public Simperf(int thread, int count, int interval) {
        this.threadPoolSize = thread;
        this.loopCount = count;
        this.interval = interval;
        initThreadPool();
    }

    public Simperf(int thread, int count, SimperfThreadFactory threadFactory) {
        this.threadPoolSize = thread;
        this.loopCount = count;
        this.threadFactory = threadFactory;
        initThreadPool();
    }

    public Simperf(int thread, int count, int interval, SimperfThreadFactory threadFactory) {
        this.threadPoolSize = thread;
        this.loopCount = count;
        this.interval = interval;
        this.threadFactory = threadFactory;
        initThreadPool();
    }

    public void start(SimperfThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        start();
    }

    public void start() {
        for (int i = 0; i < threadPoolSize; i++) {
            SimperfThread thread = createThread();
            thread.setTransCount(loopCount);
            thread.setThreadLatch(threadLatch);
            thread.setMaxTps(maxTps);
            threads.add(thread);
            threadPool.execute(thread);
        }
        String info = getStartInfo();
        monitorThread.write(info);
        monitorThread.start();
    }

    public SimperfThread createThread() {
        SimperfThread thread;
        if (null != threadFactory) {
            thread = threadFactory.newThread();
        } else {
            thread = new SimperfThread();
        }
        return thread;
    }

    protected void initThreadPool() {
        threadPool = new ThreadPoolExecutor(threadPoolSize, Integer.MAX_VALUE, 60L,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        threadLatch = new CountDownLatch(threadPoolSize);
        if (null == monitorThread) {
            monitorThread = new MonitorThread(threads, threadPool, interval);
            // 设置调整线程锁
            monitorThread.setAdjustThreadLock(adjustThreadLock);
            // 统计时计算已经停止的线程
            monitorThread.setDieThreads(dieThreads);
        }
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int thread) {
        if (threads.size() > 0) {
            int before = this.threadPoolSize;
            // 正在运行中时动态调整
            boolean result = adjustThreadPoolSize(thread);
            if (result) {
                this.threadPoolSize = thread;
                logger.info("调整并发线程: " + before + " => " + thread);
            } else {
                logger.warn("调整线程失败");
            }
        } else {
            // 还没有开始执行线程时
            this.threadPoolSize = thread;
        }

    }

    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
        if (threads.size() > 0) {
            for (SimperfThread thread : threads) {
                thread.setTransCount(loopCount);
            }
        }
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
        if (monitorThread != null) {
            monitorThread.setInterval(interval);
        }
    }

    public SimperfThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setThreadFactory(SimperfThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public MonitorThread getMonitorThread() {
        return monitorThread;
    }

    public void setMonitorThread(MonitorThread monitorThread) {
        this.monitorThread = monitorThread;
    }

    public CountDownLatch getThreadLatch() {
        return threadLatch;
    }

    public List<SimperfThread> getThreads() {
        return threads;
    }

    public String getStartInfo() {
        startInfo = "{StartInfo: {THREAD_POOL_SIZE:" + threadPoolSize + ",LOOP_COUNT:" + loopCount
                    + ",INTERVAL:" + interval + "}, ExtInfo: " + extInfo + "}";
        return startInfo;
    }

    public long getMaxTps() {
        return maxTps;
    }

    public void setMaxTps(long maxTps) {
        this.maxTps = maxTps;
    }

    public String getExtInfo() {
        return extInfo;
    }

    /**
     * json style extinfo
     */
    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    /**
     * 设置count，可动态设置
     * @param loopCount
     */
    public Simperf count(int loopCount) {
        setLoopCount(loopCount);
        return this;
    }

    /**
     * 设置监控频率，可动态设置
     * @param interval
     */
    public Simperf interval(int interval) {
        setInterval(interval);
        return this;
    }

    /**
     * 设置线程量(并发量)，可动态设置
     * @param thread
     */
    public Simperf thread(int thread) {
        setThreadPoolSize(thread);
        return this;
    }

    /**
     * 动态调整线程并发量
     * @param threadPoolSize
     */
    private boolean adjustThreadPoolSize(int threadPoolSize) {
        if (adjustThreadLock.isLocked()) {
            logger.warn("暂时不能进行线程调整");
            return false;
        }
        int currentThreadPoolSize = threads.size();
        if (threadPoolSize <= 0 || currentThreadPoolSize <= 0
            || threadPoolSize == currentThreadPoolSize) {
            logger.warn("参数检查失败");
            return false;
        }
        adjustThreadLock.lock();
        if (currentThreadPoolSize > threadPoolSize) {
            for (int i = currentThreadPoolSize - 1; i >= threadPoolSize; i--) {
                SimperfThread toStopThread = threads.remove(i);
                toStopThread.stop();
                dieThreads.add(toStopThread);
            }
        } else {
            CountDownLatch adjustLatch = new CountDownLatch(threadPoolSize - currentThreadPoolSize);
            for (int i = currentThreadPoolSize; i < threadPoolSize; i++) {
                SimperfThread thread = createThread();
                thread.setTransCount(loopCount);
                thread.setThreadLatch(adjustLatch);
                thread.setMaxTps(maxTps);
                threadPool.execute(thread);
                threads.add(thread);
            }
        }
        adjustThreadLock.unlock();
        return true;
    }
}
