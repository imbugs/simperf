package simperf.result;

/**
 * Sample统计, 默认millisTime
 * @author imbugs
 */
public class DataStatistics {
    public volatile long startTime    = 0;
    public volatile long successCount = 0;
    public volatile long failCount    = 0;
    public volatile long endTime      = 0;

    /**
     *  用于计算rt,去除sleep与计算时间, nanoTime
     */
    public volatile long runningTime  = 0;

    /**
     * 最大响应时间, nanoTime
     */
    public volatile long maxRt        = Long.MIN_VALUE;

    /**
     * 最小响应时间, nanoTime
     */
    public volatile long minRt        = Long.MAX_VALUE;

    public void addRunningTime(long runningTime) {
        this.runningTime += runningTime;
        if (this.maxRt < runningTime) {
            this.maxRt = runningTime;
        }
        if (this.minRt > runningTime) {
            this.minRt = runningTime;
        }
    }

    public long getDurationTime() {
        return endTime - startTime;
    }
}
