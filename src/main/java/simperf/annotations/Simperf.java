package simperf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import simperf.config.Constant;

/**
 * 用于JUnit的性能测试注解
 * @author imbugs
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Simperf {
    /**
     * 并发线程数年
     */
    int thread() default 1;

    /**
     * 每个线程执行次数
     */
    int count() default 1;

    /**
     * 统计间隔时间
     */
    int interval() default 1000;

    /**
     * 单线程最大TPS
     */
    long maxTps() default -1;

    /**
     * 是否启用jtl日志，jtl日志可用于jmeter分析
     */
    boolean jtl() default false;

    /**
     * 如果启用jtl日志，指定jtl日志的文件
     */
    String jtlFile() default Constant.DEFAULT_JTL_FILE;

    /**
     * 指定log日志的文件
     */
    String logFile() default Constant.DEFAULT_RESULT_LOG;
}
