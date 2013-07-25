package simperf.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.config.Constant;

public class SimperfUtil {
    protected static Logger logger       = LoggerFactory.getLogger(SimperfUtil.class);

    public static String    na           = Constant.DEFAULT_NA;
    public static String    divideFormat = Constant.DEFAULT_DIVIDE_FORMAT;

    /**
     * @param fractions 分子
     * @param denominator 分母
     * @return
     */
    public static String divide(long fractions, long denominator) {
        if (denominator == 0) {
            return na;
        }
        float r = 1.0f * fractions / denominator;
        return String.format(divideFormat, r);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("线程睡眠被异常打断", e);
        }
    }

    public static float percent(float numerator, float denominator) {
        if (denominator <= 0) {
            return 0;
        }
        float fractions = numerator / denominator;
        long percent = Math.round(fractions * 10000);
        if (percent > 10000) {
            percent = 10000;
        }
        return (percent / 100.0f);
    }
}
