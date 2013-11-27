package simperf.remote.result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.remote.RemoteSimperf;
import simperf.result.StatInfo;
import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;
/**
 * 将结果向RemoteSimperf提交请求的默认实现
 * 
 * @author imbugs
 */
public class DefaultRemoteWriter extends DefaultCallback {

    protected static final Logger logger               = LoggerFactory
                                                           .getLogger(DefaultRemoteWriter.class);

    protected RemoteSimperf       remoteSimperf;
    protected static final String REQ_TEMPLATE         = "{type: 'result', success: 'true', msg: '', data: {time:'%s' ,avgTps:'%s' ,count:'%d' ,duration:'%d' ,fail:'%d' ,tTps:'%s' ,tCount:'%d' ,tDuration:'%d' ,tFail:'%d', summary: false}}";
    protected static final String SUMMRAY_REQ_TEMPLATE = "{type: 'result', success: 'true', msg: '', data: {time:'%s' ,avgTps:'%s' ,count:'%d' ,duration:'%d' ,fail:'%d' ,tTps:'%s' ,tCount:'%d' ,tDuration:'%d' ,tFail:'%d', summary: true}}";

    public DefaultRemoteWriter(RemoteSimperf remoteSimperf) {
        this.remoteSimperf = remoteSimperf;
    }

    public void onStart(MonitorThread monitorThread) {
    }

    public void onMonitor(MonitorThread monitorThread, StatInfo statInfo) {
        try {
            statInfo.setMsgFormat(REQ_TEMPLATE);
            statInfo.setDataFormat(null);
            this.remoteSimperf.write(statInfo.toString());
        } catch (Exception e) {
            logger.error("上传结果失败, " + statInfo.toString());
        }
    }

    public void onExit(MonitorThread monitorThread) {
        StatInfo statInfo = monitorThread.getStatInfo();
        try {
            statInfo.setMsgFormat(SUMMRAY_REQ_TEMPLATE);
            statInfo.setDataFormat(null);
            this.remoteSimperf.write(statInfo.toString());
        } catch (Exception e) {
            logger.error("上传结果失败, " + statInfo.toString());
        }
    }
}
