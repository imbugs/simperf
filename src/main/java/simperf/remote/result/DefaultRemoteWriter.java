package simperf.remote.result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.remote.RemoteRequest;
import simperf.remote.RemoteSimperf;
import simperf.result.StatInfo;
import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;

import com.google.gson.Gson;

/**
 * 将结果向RemoteSimperf提交请求的默认实现
 * 
 * @author imbugs
 */
public class DefaultRemoteWriter extends DefaultCallback {

    protected static final Logger logger = LoggerFactory.getLogger(DefaultRemoteWriter.class);

    protected RemoteSimperf       remoteSimperf;

    public DefaultRemoteWriter(RemoteSimperf remoteSimperf) {
        this.remoteSimperf = remoteSimperf;
    }

    public void onStart(MonitorThread monitorThread) {
    }

    public void onMonitor(MonitorThread monitorThread, StatInfo statInfo) {
        JsonStatInfo jsonStat = new JsonStatInfo(statInfo, remoteSimperf.getSession(), false);
        RemoteRequest request = new RemoteRequest("result", "true", "", jsonStat);
        try {
            this.remoteSimperf.write(request.toJson());
        } catch (Exception e) {
            logger.error("上传结果失败, " + request.toJson());
        }
    }

    public void onExit(MonitorThread monitorThread) {
        StatInfo statInfo = monitorThread.getStatInfo();
        JsonStatInfo jsonStat = new JsonStatInfo(statInfo, remoteSimperf.getSession(), true);
        RemoteRequest request = new RemoteRequest("result", "true", "", jsonStat);
        try {
            this.remoteSimperf.write(request.toJson());
        } catch (Exception e) {
            logger.error("上传结果失败, " + request.toJson());
        }
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
