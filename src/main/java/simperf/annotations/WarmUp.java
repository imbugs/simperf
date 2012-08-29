package simperf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 线程预热，与 {@link BeforeRunTask} 的不同点是 {@link WarmUp} 在阀门打开之前执行，会同步等待其它线程全部执行完毕
 * @author imbugs
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WarmUp {

}
