package simperf.replayer;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.util.SimperfUtil;

/**
 * 秒级流量回放
 * 文件格式
 * millis,count (eg: 1378204355000,7)
 * @author imbugs
 */
public class SecondReplayer extends AbstractReplayer {
    private static final long        serialVersionUID = 7829974718982256495L;
    protected static final Logger    logger           = LoggerFactory
                                                          .getLogger(SecondReplayer.class);
    public static String             SPLIT_STR        = ",";
    private ScheduledExecutorService executor         = Executors.newScheduledThreadPool(1);
    private File                     replayFile;
    private long                     onlineFirstTime  = 0;
    private long                     replayFirstTime  = 0;
    private long                     delayTime        = 1000L;

    public SecondReplayer(String filePath) {
        replayFile = new File(filePath);
    }

    public void start() {
        try {
            Scanner scanner = new Scanner(replayFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (StringUtils.contains(line, SPLIT_STR)) {
                    String[] parts = line.split(SPLIT_STR);
                    long millis = Long.valueOf(parts[0]);
                    int permits = Integer.valueOf(parts[1]);
                    long current = System.currentTimeMillis();
                    if (onlineFirstTime <= 0) {
                        onlineFirstTime = millis;
                        replayFirstTime = current;
                    }
                    long delay = replayFirstTime - onlineFirstTime + millis - current + delayTime;
                    executor.schedule(new ReleasePermits(permits, 1000), delay,
                        TimeUnit.MILLISECONDS);
                }
            }
            scanner.close();
        } catch (Throwable e) {
            logger.error("流量回放发生错误.", e);
        }
    }

    public void stop() {
        executor.shutdownNow();
    }

    /**
     * 在一段时间内平均释放permits
     * @author imbugs
     */
    class ReleasePermits implements Runnable {
        // 这段时间内要释放的个数
        private int  permits;
        private long duration;

        public ReleasePermits(int permits, long duration) {
            this.permits = permits;
            this.duration = duration;
        }

        public void run() {
            long sleep = duration / permits;
            for (int i = 0; i < permits; i++) {
                SecondReplayer.this.release();
                SimperfUtil.sleep(sleep);
            }
        }
    }
}
