package simperf.junit;

import java.lang.reflect.InvocationTargetException;

import org.junit.internal.runners.TestMethod;

import simperf.thread.SimperfThread;

/**
 * 用于执行Simperf的默认线程
 * @author imbugs
 */
public class SimperfJunit4Thread extends SimperfThread {

    protected final Object fTest;
    protected TestMethod   fTestMethod;

    public SimperfJunit4Thread(Object fTest, TestMethod fTestMethod) {
        this.fTest = fTest;
        this.fTestMethod = fTestMethod;
    }

    public boolean runTask() {
        try {
            fTestMethod.invoke(fTest);
        } catch (InvocationTargetException e) {
            return false;
        } catch (Throwable e) {
            return false;
        }
        return true;

    }
}
