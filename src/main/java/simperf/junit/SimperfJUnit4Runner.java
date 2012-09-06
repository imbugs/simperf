package simperf.junit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.internal.runners.TestClass;
import org.junit.internal.runners.TestMethod;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import simperf.Simperf;
import simperf.annotations.Inject;
import simperf.config.SimperfConfig;
import simperf.result.JTLResult;

/**
 * ‘⁄JUnit¿Ô÷¥––Simperf
 * @author imbugs
 */
public class SimperfJUnit4Runner extends JUnit4ClassRunner {

    protected List<Method> simperfMethods = new ArrayList<Method>();

    public SimperfJUnit4Runner(Class<?> klass) throws InitializationError {
        super(klass);
        SimperfMethodValidator methodValidator = new SimperfMethodValidator(getTestClass());
        methodValidator.validateMethodsForDefaultRunner();
        methodValidator.assertValid();
    }

    @Override
    protected void invokeTestMethod(Method method, RunNotifier notifier) {
        simperf.annotations.Simperf simperfConfig = method
            .getAnnotation(simperf.annotations.Simperf.class);
        if (null == simperfConfig) {
            super.invokeTestMethod(method, notifier);
        } else {
            Description description = methodDescription(method);
            Object test;
            try {
                test = createTest();
            } catch (InvocationTargetException e) {
                notifier.testAborted(description, e.getCause());
                return;
            } catch (Exception e) {
                notifier.testAborted(description, e);
                return;
            }

            Simperf simperf = new Simperf(simperfConfig.thread(), simperfConfig.count(),
                simperfConfig.interval());
            injectVariable(test, simperf, Simperf.class);
            simperf.setMaxTps(simperfConfig.maxTps());
            simperf.getMonitorThread().setLogFile(simperfConfig.logFile());
            if (simperfConfig.jtl()) {
                JTLResult jtlResult = new JTLResult(simperfConfig.jtlFile(),
                    simperf.getMonitorThread());
                SimperfConfig.setConfig(SimperfConfig.JTL_RESULT, jtlResult);
            }
            TestMethod testMethod = wrapMethod(method);
            new SimperfMethodRoadie(this, simperf, test, testMethod, notifier, description).run();
        }
    }

    protected void injectVariable(Object obj, Object value, Class<?> cls) {
        try {
            Class<?> klass = super.getTestClass().getJavaClass();
            do {
                Field[] fields = klass.getDeclaredFields();
                if (null != fields) {
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(Inject.class)) {
                            if (field.getType().isAssignableFrom(cls)) {
                                field.setAccessible(true);
                                field.set(obj, value);
                            }
                        }
                    }
                }
                klass = klass.getSuperclass();
            } while (!klass.isAssignableFrom(Object.class));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public TestClass getTestClass() {
        return super.getTestClass();
    }

}
