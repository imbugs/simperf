package simperf.util;

import simperf.config.Constant;

public class SimperfUtil {
    public static String na           = Constant.DEFAULT_NA;
    public static String divideFormat = Constant.DEFAULT_DIVIDE_FORMAT;

    /**
     * @param fractions ·Ö×Ó
     * @param denominator ·ÖÄ¸
     * @return
     */
    public static String divide(long fractions, long denominator) {
        if (denominator == 0) {
            return na;
        }
        float r = 1.0f * fractions / denominator;
        return String.format(divideFormat, r);
    }
}
