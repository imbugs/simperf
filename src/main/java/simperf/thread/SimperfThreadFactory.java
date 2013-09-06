package simperf.thread;

public interface SimperfThreadFactory {
    /**
     * 创建压测线程，不能返回相同的线程，可以让不同的线程构成不同的业务场景
     */
    public SimperfThread newThread();
}
