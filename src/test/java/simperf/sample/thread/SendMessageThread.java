package simperf.sample.thread;

import simperf.thread.SimperfThread;

public class SendMessageThread extends SimperfThread {

    MessageSender sender;

    public boolean runTask() {
        return sender.send();
    }

    public void setSender(MessageSender sender) {
        this.sender = sender;
    }
}