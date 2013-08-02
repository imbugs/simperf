package simperf.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 远程控制锁 Server示例:
 * 
 * <pre>
 * ServerSocket welcomeSocket = new ServerSocket(20122);
 * while (true) {
 * 	Socket connectionSocket = welcomeSocket.accept();
 * 	DataOutputStream outToClient = new DataOutputStream(
 * 			connectionSocket.getOutputStream());
 * 	for (int i = 0; i &lt; 5; i++) {
 * 		outToClient.writeBytes(&quot;wait\n&quot;);
 * 		Thread.sleep(1000);
 * 	}
 * 	outToClient.writeBytes(&quot;run\n&quot;);
 * }
 * </pre>
 * 
 * @author imbugs
 */
public class ClientLatch {
    private static final Logger  logger     = LoggerFactory.getLogger(ClientLatch.class);
    private static ReentrantLock clientLock = new ReentrantLock();
    private static AtomicBoolean needWait   = new AtomicBoolean(true);
    private static Socket        clientSocket;
    private DataOutputStream     outToServer;
    private BufferedReader       inFromServer;
    private String               server;
    private int                  port       = 20122;

    public ClientLatch(String server) {
        this.server = server;
    }

    public ClientLatch(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public void init() throws Exception {
        if (null == clientSocket) {
            clientSocket = new Socket(server, port);
            InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            inFromServer = new BufferedReader(isr);
        }
    }

    /**
     * 通知服务器已就绪
     * @throws InterruptedException
     */
    public void ready() throws InterruptedException {
        try {
            write("ready");
        } catch (Exception e) {
            logger.error("client latch ready fail", e);
            throw new InterruptedException("client latch ready fail");
        }
    }

    /**
     * 向服务器发送信息 
     */
    public void write(String cmd) throws Exception {
        init();
        outToServer.writeBytes(cmd + "\n");
        logger.info("向服务器发送命令 [" + cmd + "]");
    }

    /**
     * 等待服务器命令
     * @throws InterruptedException
     */
    public void await() throws InterruptedException {
        if (!needWait.get()) {
            // 不需要等待的即时返回
            return;
        }
        // 对于并发线程只能有一个线程向服务器发送请求
        clientLock.lock();
        // 线程获取到锁时判断是否需要向服务器发送请求,再次判断是为了防止其它线程已经修改了needWait
        if (needWait.get()) {
            logger.info("等待远程服务器发送 [RUN] 命令");
            try {
                init();
                String cmd = "wait";
                do {
                    cmd = inFromServer.readLine();
                } while (null != cmd && !cmd.toLowerCase().equals("run"));
                // 如果Server端发送run命令,则结束等待
                logger.info("接收到服务器命令 [" + cmd + "]");
                needWait.set(false);
                clientSocket.close();
            } catch (Exception e) {
                logger.error("client latch await fail", e);
                throw new InterruptedException("client latch await fail");
            }
        }
        clientLock.unlock();
    }
}
