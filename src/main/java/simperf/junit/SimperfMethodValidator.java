package simperf.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClass;

import simperf.annotations.AfterInvoke;
import simperf.annotations.AfterRunTask;
import simperf.annotations.BeforeInvoke;
import simperf.annotations.BeforeRunTask;
import simperf.annotations.WarmUp;

public class SimperfMethodValidator {

    private final List<Throwable> fErrors = new ArrayList<Throwable>();

    private TestClass             fTestClass;

    public SimperfMethodValidator(TestClass testClass) {
        fTestClass = testClass;
    }

    public void validateInstanceMethods() {
        validateTestMethods(BeforeRunTask.class, false);
        validateTestMethods(WarmUp.class, false);
        validateTestMethods(BeforeInvoke.class, false);
        validateTestMethods(AfterInvoke.class, false);
        validateTestMethods(AfterRunTask.class, false);
    }

    public List<Throwable> validateMethodsForDefaultRunner() {
        validateInstanceMethods();
        return fErrors;
    }

    public void assertValid() throws InitializationError {
        if (!fErrors.isEmpty())
            throw new InitializationError(fErrors);
    }

    public void validateTestMethods(Class<? extends Annotation> annotation, boolean isStatic) {
        List<Method> methods = fTestClass.getAnnotatedMethods(annotation);

        for (Method each : methods) {
            if (Modifier.isStatic(each.getModifiers()) != isStatic) {
                String state = isStatic ? "should" : "should not";
                fErrors
                    .add(new Exception("Method " + each.getName() + "() " + state + " be static"));
            }
            if (!Modifier.isPublic(each.getDeclaringClass().getModifiers()))
                fErrors.add(new Exception("Class " + each.getDeclaringClass().getName()
                                          + " should be public"));
            if (!Modifier.isPublic(each.getModifiers()))
                fErrors.add(new Exception("Method " + each.getName() + " should be public"));
            if (each.getReturnType() != Void.TYPE)
                fErrors.add(new Exception("Method " + each.getName() + " should be void"));
            if (each.getParameterTypes().length != 0)
                fErrors
                    .add(new Exception("Method " + each.getName() + " should have no parameters"));
        }
    }
}
