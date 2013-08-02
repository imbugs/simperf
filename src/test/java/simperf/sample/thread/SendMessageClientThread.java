package simperf.sample.thread;

import simperf.client.ClientLatch;
import simperf.client.SimperfClientThread;

public class SendMessageClientThread extends SimperfClientThread {
    public SendMessageClientThread(ClientLatch clientLatch) {
        super(clientLatch);
    }

    MessageSender sender;

    public boolean runTask() {
        return sender.send();
    }

    public void setSender(MessageSender sender) {
        this.sender = sender;
    }
}