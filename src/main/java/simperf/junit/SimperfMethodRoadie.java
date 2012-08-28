package simperf.junit;

import java.util.concurrent.CountDownLatch;

import org.junit.internal.runners.MethodRoadie;
import org.junit.internal.runners.TestMethod;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import simperf.Simperf;
import simperf.thread.Callback;
import simperf.thread.PrintStatus;
import simperf.thread.SimperfThread;
import simperf.thread.SimperfThreadFactory;

/**
 * ÖØÐ´JUnitÖÐµÄMethodRoadie
 * @author imbugs
 */
public class SimperfMethodRoadie extends MethodRoadie {
    protected Simperf           simperf;
    protected final Object      fTest;
    protected final RunNotifier fNotifier;
    protected final Description fDescription;
    protected TestMethod        fTestMethod;
    protected CountDownLatch    latch = new CountDownLatch(1);

    public SimperfMethodRoadie(Simperf simperf, Object test, TestMethod method,
                               RunNotifier notifier, Description description) {
        super(test, method, notifier, description);
        this.simperf = simperf;
        this.fTest = test;
        this.fNotifier = notifier;
        this.fDescription = description;
        this.fTestMethod = method;
        this.simperf.setThreadFactory(new SimperfThreadFactory() {
            public SimperfThread newThread() {
                return new SimperfJunit4Thread(fTest, fTestMethod);
            }
        });
        this.simperf.getPrintThread().registerCallback(new Callback() {
            public void run(PrintStatus pstatus) {
                latch.countDown();
            }
        });
    }

    @Override
    protected void runTestMethod() {
        try {
            this.simperf.start();
            latch.await();
        } catch (Throwable e) {
            addFailure(e);
        }
    }

}
