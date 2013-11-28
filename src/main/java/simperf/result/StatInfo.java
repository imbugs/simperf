package simperf.result;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.config.Constant;
import simperf.util.SimperfUtil;

/**
 * 统计信息类
 * @author imbugs
 */
public class StatInfo {
    private static final Logger logger          = LoggerFactory.getLogger(StatInfo.class);

    /**
     * 输出消息格式化
     */
    private String              msgFormat       = Constant.DEFAULT_MSG_FORMAT;

    /**
     * 详细消息格式化,添加RT响应
     */
    private String              detailMsgFormat = Constant.DEFAULT_DETAIL_MSG_FORMAT;

    /**
     * 时间格式化，例如：new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
     */
    private SimpleDateFormat    dateFormat      = Constant.DEFAULT_DATE_FORMAT;

    /**
     * 本记录统计的时间
     */
    public long                 time;

    /**
     * 平均TPS
     */
    public String               avgTps          = SimperfUtil.na;

    /**
     * 发送总计数
     */
    public long                 count;

    /**
     * 发送总耗时
     */
    public long                 duration;

    /**
     * 真正执行时间,去除sleep与计算时间, milliTime
     */
    public long                 runningTime;

    /**
     * 平均响应时间, milliTime
     */
    public String               avgRt           = SimperfUtil.na;

    /**
     * 最大最小响应时间, milliTime
     */
    public long                 maxRt, minRt;

    /**
     * 发送失败数
     */
    public long                 fail;

    /**
     * 当前时间段TPS
     */
    public String               tTps            = SimperfUtil.na;

    /**
     * 当前时间段计数
     */
    public long                 tCount;

    /**
     * 当前时间段耗时
     */
    public long                 tDuration;

    /**
     * 当前时间段真正执行时间,去除sleep与计算时间
     */
    public long                 tRunningTime;

    /**
     * 当前时间段平均响应时间
     */
    public String               tAvgRt          = SimperfUtil.na;

    /**
     * 当前时间段失败数
     */
    public long                 tFail;

    public StatInfo() {
    }

    public StatInfo(String msgFormat, SimpleDateFormat dateFormat) {
        this.msgFormat = msgFormat;
        this.dateFormat = dateFormat;
    }

    public String toString() {
        if (Constant.USE_DETAIL_MSG_FORMAT) {
            return detailFormat();
        } else {
            return format();
        }
    }

    public String format() {
        String timeStr = String.valueOf(time);
        if (null != dateFormat) {
            timeStr = dateFormat.format(time);
        }
        return String.format(msgFormat, timeStr, avgTps, count, duration, fail, tTps, tCount,
            tDuration, tFail);
    }

    public String detailFormat() {
        String timeStr = String.valueOf(time);
        if (null != dateFormat) {
            timeStr = dateFormat.format(time);
        }
        return String.format(detailMsgFormat, timeStr, avgTps, avgRt, maxRt, minRt, count,
            duration, fail, tTps, tAvgRt, tCount, tDuration, tFail);
    }

    public void write(FileWriter fw) {
        if (fw == null) {
            return;
        }
        try {
            fw.write(this.toString());
        } catch (IOException e) {
            logger.error("写文件异常", e);
        }
    }

    public void write(OutputStream os) {
        if (os == null) {
            return;
        }
        try {
            os.write(this.toString().getBytes());
        } catch (IOException e) {
            logger.error("写文件异常", e);
        }
    }

    public String getMsgFormat() {
        return msgFormat;
    }

    public void setMsgFormat(String msgFormat) {
        this.msgFormat = msgFormat;
    }

    public SimpleDateFormat getDataFormat() {
        return dateFormat;
    }

    public void setDataFormat(SimpleDateFormat dataFormat) {
        this.dateFormat = dataFormat;
    }
}
