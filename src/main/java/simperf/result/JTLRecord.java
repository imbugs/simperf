package simperf.result;

public class JTLRecord {
    public long    elapsedTime;
    public long    tsend;
    public boolean result;
    public long    tid;

    public JTLRecord(long elapsedTime, long tsend, boolean result) {
        this.elapsedTime = elapsedTime;
        this.tsend = tsend;
        this.result = result;
        this.tid = Thread.currentThread().getId();
    }

    public JTLRecord(long elapsedTime, long tsend, boolean result, long tid) {
        this.elapsedTime = elapsedTime;
        this.tsend = tsend;
        this.result = result;
        this.tid = tid;
    }
}
