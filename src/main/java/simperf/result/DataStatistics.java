package simperf.result;

public class DataStatistics {
    public long startTime    = 0;
    public long successCount = 0;
    public long failCount    = 0;
    public long endTime      = 0;

    public long getDurationTime() {
        return endTime - startTime;
    }
}
