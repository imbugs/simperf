package simperf.remote;

import com.google.gson.Gson;

/**
 * 向服务器发送数据
 * @author imbugs
 */
public class RemoteRequest {
    private static final Gson gson = new Gson();
    private String            type;
    private String            success;
    private String            msg;
    private Object            data;

    public RemoteRequest() {
    }

    public RemoteRequest(String type, String success, String msg, Object data) {
        this.type = type;
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toJson() {
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return "RemoteRequest [type=" + type + ", msg=" + msg + ", success=" + success + ", data="
               + data + "]";
    }
}
