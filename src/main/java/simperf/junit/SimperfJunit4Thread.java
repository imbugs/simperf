package simperf.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.internal.runners.TestClass;
import org.junit.internal.runners.TestMethod;

import simperf.annotations.AfterInvoke;
import simperf.annotations.AfterRunTask;
import simperf.annotations.BeforeInvoke;
import simperf.annotations.BeforeRunTask;
import simperf.annotations.WarmUp;
import simperf.thread.SimperfThread;

/**
 * 用于执行Simperf的默认线程
 * @author imbugs
 */
public class SimperfJunit4Thread extends SimperfThread {

    protected final Object              fTest;
    protected final TestMethod          fTestMethod;
    protected final SimperfJUnit4Runner fRunner;

    public SimperfJunit4Thread(SimperfJUnit4Runner simperfRunner, Object fTest,
                               TestMethod fTestMethod) {
        this.fRunner = simperfRunner;
        this.fTest = fTest;
        this.fTestMethod = fTestMethod;
    }

    public void warmUp() {
        invokeMethods(WarmUp.class);
        super.warmUp();
    }

    public void beforeRunTask() {
        invokeMethods(BeforeRunTask.class);
        super.beforeRunTask();
    }

    public void afterRunTask() {
        invokeMethods(AfterRunTask.class);
        super.afterRunTask();
    }

    protected Object beforeInvoke() {
        invokeMethods(BeforeInvoke.class);
        return super.beforeInvoke();
    }

    protected void afterInvoke(boolean result, Object beforeInvokeResult) {
        invokeMethods(AfterInvoke.class);
        super.afterInvoke(result, beforeInvokeResult);
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

    protected void invokeMethods(Class<? extends Annotation> annotation) {
        try {
            TestClass klass = fRunner.getTestClass();
            List<Method> beforeInvokeMethods = klass.getAnnotatedMethods(annotation);
            for (Method method : beforeInvokeMethods) {
                method.invoke(fTest);
            }
        } catch (Exception e) {
            ; // do nothing
        }
    }
}
