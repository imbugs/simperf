package simperf.remote;

public class RemoteCmd {
    public static final String CMD_CLOSE   = "close";
    public static final String CMD_PERCENT = "percent";
    public static final String CMD_START   = "start";
    public static final String CMD_STOP    = "stop";
    public static final String CMD_MSG     = "message";

    private String             cmd;
    private String             param;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "RemoteCmd [cmd=" + cmd + ", param=" + param + "]";
    }
}