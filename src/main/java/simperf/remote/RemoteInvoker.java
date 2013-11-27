package simperf.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.Simperf;

/**
 * 处理远程控制命令
 * @author imbugs
 */
public class RemoteInvoker {
    protected static final Logger logger = LoggerFactory.getLogger(RemoteInvoker.class);

    private Simperf               simperf;
    private RemoteCmd             remoteCmd;

    public RemoteInvoker(Simperf simperf, RemoteCmd remoteCmd) {
        this.simperf = simperf;
        this.remoteCmd = remoteCmd;
    }

    public String invoke() throws Exception {
        String cmd = remoteCmd.getCmd();
        if (cmd.equals(RemoteCmd.CMD_PERCENT)) {
            float progress = this.simperf.getMonitorThread().percentProgress();
            return "{type: 'percent', success: 'true', percent: '" + progress + "', data: ''}";
        } else if (cmd.equals(RemoteCmd.CMD_START)) {
            if (!this.simperf.isRunning()) {
                this.simperf.start();
                return "{type: 'return', success: 'true', msg: 'start', data: ''}";
            } else {
                return "{type: 'return', success: 'false', msg: 'simperf is running', data: ''}";
            }
        } else if (cmd.equals(RemoteCmd.CMD_STOP)) {
            if (this.simperf.isRunning()) {
                this.simperf.stopAll();
                return "{type: 'return', success: 'true', msg: 'stop', data: ''}";
            } else {
                return "{type: 'return', success: 'false', msg: 'simperf is not running', data: ''}";
            }
        } else if (cmd.equals(RemoteCmd.CMD_CLOSE)) {
            return "{type: 'return', success: 'true', msg: 'close', data: ''}";
        } else if (cmd.equals(RemoteCmd.CMD_MSG)) {
            logger.info("[MSG] " + remoteCmd.getParam());
            return "{type: 'return', success: 'true', msg: 'recieve', data: ''}";
        }
        return "{type: 'return', success: 'false', msg: 'unknown command', data: ''}";
    }
}
