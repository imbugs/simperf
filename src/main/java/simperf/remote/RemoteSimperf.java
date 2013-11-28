package simperf.remote;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.Simperf;
import simperf.remote.result.DefaultRemoteWriter;
import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;
import simperf.util.SimperfUtil;

import com.google.gson.Gson;

/**
 * 远程Simperf, 此时Simperf做为一个Client接受远程服务器的指令
 * @author imbugs
 */
public class RemoteSimperf extends Thread{
    protected static final Logger logger     = LoggerFactory.getLogger(RemoteSimperf.class);
    protected Simperf             simperf;
    protected String              server;
    protected int                 port       = 20122;
    protected boolean             retry      = true;
    protected ReentrantLock       socketLock = new ReentrantLock();
    /**
     * 远程client的唯一标识,由server端进行分配,以便server端能够正确识别
     */
    protected String              session    = "";

    protected Gson                gson       = new Gson();
    protected static Socket       clientSocket;
    protected DataOutputStream    outToServer;
    protected BufferedReader      inFromServer;

    public RemoteSimperf(Simperf simperf, String server) {
        this.simperf = simperf;
        this.simperf.getMonitorThread().registerCallback(new DefaultRemoteWriter(this));
        this.simperf.getMonitorThread().registerCallback(new DefaultCallback() {
            public void onExit(MonitorThread monitorThread) {
                try {
                    retry = false;
                    RemoteSimperf.clientSocket.close();
                } catch (IOException e) {
                    logger.error("与RemoteSimperf断开连接时发生错误.", e);
                }
            }
        });
        this.server = server;
    }

    public RemoteSimperf(Simperf simperf, String host, int port) {
        this(simperf, host);
        this.port = port;
    }

    /**
     * 建立连接,接收指令
     * {cmd: '', param: ''}
     * cmd RemoteCmd
     */
    public void run() {
        while (retry) {
            try {
                try {
                    socketLock.lock();
                    // connect or retry
                    clientSocket = new Socket(server, port);
                    InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
                    outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    inFromServer = new BufferedReader(isr);
                } finally {
                    socketLock.unlock();
                }

                String line;
                RemoteCmd remoteCmd;
                do {
                    line = inFromServer.readLine();
                    logger.debug("Remote cmd : " + line);
                    remoteCmd = gson.fromJson(line, RemoteCmd.class);
                    if (remoteCmd == null) {
                        continue;
                    }
                    RemoteInvoker invoker = new RemoteInvoker(this, remoteCmd);
                    RemoteRequest result = invoker.invoke();
                    write(result.toJson());
                } while (!remoteCmd.getCmd().equals(RemoteCmd.CMD_CLOSE));
                clientSocket.close();
            } catch (Exception e) {
                if (retry) {
                    logger.warn("与RemoteSimperf的连接已关闭. 尝试重新连接");
                    SimperfUtil.sleep(1000);
                } else {
                    logger.warn("与RemoteSimperf的连接关闭.");
                }
            }
        }
    }

    /**
     * 向服务器发送信息
     * {type: '', success: '', msg:'', data: ''}
     * type:
     *      result, 结果
     *      percent, 百分比信息
     *      return, 返回值
     */
    public void write(String line) throws Exception {
        try {
            socketLock.lock();
            outToServer.writeBytes(line + "\n");
            logger.debug("向服务器发送命令 [" + line + "]");
        } finally {
            socketLock.unlock();
        }
    }

    public Simperf getSimperf() {
        return simperf;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
