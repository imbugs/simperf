package simperf.remote;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.Simperf;

/**
 * 处理远程控制命令
 * @author imbugs
 */
public class RemoteInvoker {
    protected static final Logger logger = LoggerFactory.getLogger(RemoteInvoker.class);

    private RemoteSimperf         remoteSimperf;
    private Simperf               simperf;
    private RemoteCmd             remoteCmd;

    public RemoteInvoker(RemoteSimperf remoteSimperf, RemoteCmd remoteCmd) {
        this.remoteSimperf = remoteSimperf;
        this.simperf = remoteSimperf.getSimperf();
        this.remoteCmd = remoteCmd;
    }

    public RemoteRequest invoke() throws Exception {
        String cmd = remoteCmd.getCmd();
        if (cmd.equals(RemoteCmd.CMD_PERCENT)) {
            float progress = this.simperf.getMonitorThread().percentProgress();
            return new RemoteRequest("percent", "true", "", String.valueOf(progress));
        } else if (cmd.equals(RemoteCmd.CMD_START)) {
            if (!this.simperf.isRunning()) {
                this.simperf.start();
                return new RemoteRequest("return", "true", "start", "");
            } else {
                return new RemoteRequest("return", "false", "simperf is running", "");
            }
        } else if (cmd.equals(RemoteCmd.CMD_STOP)) {
            if (this.simperf.isRunning()) {
                this.simperf.stopAll();
                return new RemoteRequest("return", "true", "stop", "");
            } else {
                return new RemoteRequest("return", "false", "simperf is not running", "");
            }
        } else if (cmd.equals(RemoteCmd.CMD_CLOSE)) {
            return new RemoteRequest("return", "true", "close", "");
        } else if (cmd.equals(RemoteCmd.CMD_MSG)) {
            logger.info("[MSG] " + remoteCmd.getParam());
            return new RemoteRequest("return", "true", "recieve", "");
        } else if (cmd.equals(RemoteCmd.CMD_SESSION)) {
            String param = remoteCmd.getParam();
            if (StringUtils.isNotBlank(param)) {
                // set session if not black
                this.remoteSimperf.setSession(param);
            }
            return new RemoteRequest("session", "true", "get/set session",
                this.remoteSimperf.getSession());
        }
        return new RemoteRequest("return", "false", "unknown command", "");
    }
}
