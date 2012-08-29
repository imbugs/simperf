package simperf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在 @Simperf 方法执行之前调用，只执行一次，在阀门打开之后执行，线程不同步等待
 * @author imbugs
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeRunTask {

}
