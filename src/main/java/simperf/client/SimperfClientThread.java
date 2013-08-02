package simperf.client;

import simperf.thread.SimperfThread;

/**
 * 当Simperf当做一个Client执行时，需要接受Server端的统一控制
 * 
 * @author imbugs
 */
public class SimperfClientThread extends SimperfThread {
    protected ClientLatch clientLatch;

    public SimperfClientThread(ClientLatch clientLatch) {
        this.clientLatch = clientLatch;
    }

    protected void await() throws InterruptedException {
        super.await();
        clientLatch.await();
    }
}
