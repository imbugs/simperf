package simperf.config;

import java.text.SimpleDateFormat;

public class Constant {
    /**
     * 默认结果输出文件
     */
    public static final String     DEFAULT_RESULT_LOG     = "simperf-result.log";

    /**
     * 默认JTL文件
     */
    public static final String     DEFAULT_JTL_FILE       = "simperf.jtl";

    /**
     * 默认的N/A字符串
     */
    public static String           DEFAULT_NA             = "N/A";

    /**
     * 除法运算精度
     */
    public static String           DEFAULT_DIVIDE_FORMAT  = "%.2f";

    /**
     * 默认的msg输出格式
     */
    public static String           DEFAULT_MSG_FORMAT     = "{time:%s ,avgTps:%s ,count:%d ,duration:%d ,fail:%d ,tTps:%s ,tCount:%d ,tDuration:%d ,tFail:%d}";

    /**
     * 默认的detailMsg输出格式
     */
    public static String           DEFAULT_DETAIL_MSG_FORMAT = "{time:%s ,avgTps:%s ,avgRt:%s ,maxRt:%d ,minRt:%d ,count:%d ,duration:%d ,fail:%d ,tTps:%s ,tAvgRt:%s ,tCount:%d ,tDuration:%d ,tFail:%d}";

    /**
     * 使用detailMsg
     */
    public static boolean          USE_DETAIL_MSG_FORMAT     = false;
    
    /**
     * 默认的时间格式, 例如：new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
     */
    public static SimpleDateFormat DEFAULT_DATE_FORMAT    = null;
}
