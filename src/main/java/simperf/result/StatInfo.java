package simperf.result;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import simperf.config.Constant;

/**
 * 统计信息类
 * @author imbugs
 */
public class StatInfo {
    /**
     * 输出消息格式化
     */
    private static String           msgFormat  = Constant.DEFAULT_MSG_FORMAT;
    /**
     * 时间格式化，例如：new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
     */
    private static SimpleDateFormat dataFormat = null;

    /**
     * 本记录统计的时间
     */
    public long                     time;

    /**
     * 平均TPS
     */
    public String                   avgTps;

    /**
     * 发送总计数
     */
    public long                     count;

    /**
     * 发送总耗时
     */
    public long                     duration;

    /**
     * 发送失败数
     */
    public long                     fail;

    /**
     * 当前时间段TPS
     */
    public String                   tTps;

    /**
     * 当前时间段计数
     */
    public long                     tCount;

    /**
     * 当前时间段耗时
     */
    public long                     tDuration;

    /**
     * 当前时间段失败数
     */
    public long                     tFail;

    public StatInfo() {
    }

    public StatInfo(String msgFormat, SimpleDateFormat dateFormat) {
        StatInfo.msgFormat = msgFormat;
        StatInfo.dataFormat = dateFormat;
    }

    public String toString() {
        String timeStr = String.valueOf(time);
        if (null != dataFormat) {
            timeStr = dataFormat.format(time);
        }
        return String.format(msgFormat, timeStr, avgTps, count, duration, fail, tTps, tCount,
            tDuration, tFail);
    }

    public void write(FileWriter fw) {
        if (fw == null) {
            return;
        }
        try {
            fw.write(this.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(OutputStream os) {
        if (os == null) {
            return;
        }
        try {
            os.write(this.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        System.out.println(sdf.format(System.currentTimeMillis()));
    }

    public static String getMsgFormat() {
        return msgFormat;
    }

    public static void setMsgFormat(String msgFormat) {
        StatInfo.msgFormat = msgFormat;
    }

    public static SimpleDateFormat getDataFormat() {
        return dataFormat;
    }

    public static void setDataFormat(SimpleDateFormat dataFormat) {
        StatInfo.dataFormat = dataFormat;
    }
}
