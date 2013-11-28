package simperf.remote.result;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.remote.RemoteRequest;
import simperf.remote.RemoteSimperf;
import simperf.result.StatInfo;
import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;
import simperf.util.SimperfUtil;

import com.google.gson.Gson;

/**
 * 将结果向RemoteSimperf提交请求的默认实现
 * 
 * @author imbugs
 */
public class DefaultRemoteWriter extends DefaultCallback {

    protected static final Logger          logger       = LoggerFactory
                                                            .getLogger(DefaultRemoteWriter.class);

    protected RemoteSimperf                remoteSimperf;
    protected Thread                       writeThread;
    protected long                         timeout      = 10000L;
    protected LinkedBlockingQueue<Request> requestQueue = new LinkedBlockingQueue<Request>();
    protected boolean                      running      = true;

    public DefaultRemoteWriter(RemoteSimperf remoteSimperf) {
        this.remoteSimperf = remoteSimperf;
    }

    class Request {
        protected AtomicInteger tryCount = new AtomicInteger(0);
        protected String        statJson;

        public Request(String statJson) {
            this.statJson = statJson;
        }

        public AtomicInteger getTryCount() {
            return tryCount;
        }

        public String getStatJson() {
            return statJson;
        }

        public void setStatJson(String statJson) {
            this.statJson = statJson;
        }

    }

    class RemoteWriteThread implements Runnable {
        public void run() {
            logger.info("启动RemoteWriteThread");
            int currentTry = 0;
            while (running || requestQueue.size() > 0) {
                try {
                    Request request = requestQueue.poll();
                    if (null == request) {
                        SimperfUtil.sleep(200);
                        continue;
                    }
                    int tryCount = request.getTryCount().getAndIncrement();
                    if (tryCount >= 5) {
                        // 尝试达到5次
                        logger.error("上传结果失败, " + request.getStatJson());
                        continue;
                    }
                    if (tryCount > currentTry) {
                        currentTry = tryCount;
                        SimperfUtil.sleep(1000);
                    }
                    try {
                        DefaultRemoteWriter.this.remoteSimperf.write(request.getStatJson());
                    } catch (Exception e) {
                        requestQueue.offer(request);
                    }
                } catch (Exception e) {
                }
            }

        }
    }

    public void onStart(MonitorThread monitorThread) {
        writeThread = new Thread(new RemoteWriteThread());
        writeThread.start();
    }

    public void onMonitor(MonitorThread monitorThread, StatInfo statInfo) {
        JsonStatInfo jsonStat = new JsonStatInfo(statInfo, remoteSimperf.getSession(), false);
        RemoteRequest request = new RemoteRequest("result", "true", "", jsonStat);
        requestQueue.offer(new Request(request.toJson()));
    }

    public void onExit(MonitorThread monitorThread) {
        StatInfo statInfo = monitorThread.getStatInfo();
        JsonStatInfo jsonStat = new JsonStatInfo(statInfo, remoteSimperf.getSession(), true);
        RemoteRequest request = new RemoteRequest("result", "true", "", jsonStat);
        requestQueue.offer(new Request(request.toJson()));
        running = false;
        try {
            writeThread.join(timeout);
        } catch (Exception e) {
            logger.error("等待写线程失败", e);
        }
        logger.info("RemoteWriter exit");
    }

    static class JsonStatInfo {
        private static final Gson gson = new Gson();

        public JsonStatInfo(StatInfo statInfo, String session, boolean summary) {
            this.session = session;
            this.summary = summary;

            this.time = String.valueOf(statInfo.time);
            this.avgtps = statInfo.avgTps;
            this.avgrt = statInfo.avgRt;
            this.maxrt = statInfo.maxRt;
            this.minrt = statInfo.minRt;
            this.count = statInfo.count;
            this.duration = statInfo.duration;
            this.fail = statInfo.fail;
            this.ttps = statInfo.tTps;
            this.tavgrt = statInfo.tAvgRt;
            this.tcount = statInfo.tCount;
            this.tduration = statInfo.tDuration;
            this.tfail = statInfo.tFail;
        }

        public String toJson() {
            return gson.toJson(this);
        }

        public String  time;
        public String  avgtps;
        public String  avgrt;
        public long    maxrt;
        public long    minrt;
        public long    count;
        public long    duration;
        public long    fail;
        public String  ttps;
        public String  tavgrt;
        public long    tcount;
        public long    tduration;
        public long    tfail;
        public boolean summary;
        public String  session;
    }
}
