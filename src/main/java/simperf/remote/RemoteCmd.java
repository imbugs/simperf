package simperf.remote;

public class RemoteCmd {
    /**
     * 关闭远程连接
     */
    public static final String CMD_CLOSE   = "close";
    /**
     * 查询百分比
     */
    public static final String CMD_PERCENT = "percent";
    /**
     * 启动测试
     */
    public static final String CMD_START   = "start";
    /**
     * 停止测试
     */
    public static final String CMD_STOP    = "stop";
    /**
     * 发送消息
     */
    public static final String CMD_MSG     = "message";
    /**
     * 查询或设置session
     */
    public static final String CMD_SESSION = "session";
    
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