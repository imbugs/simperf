package simperf.sample.thread;

import java.util.Random;

public class MessageSender {
    public int sleepTime = 1000;
    Random     rand      = new Random();

    public boolean send() {
        try {
            Thread.sleep(sleepTime);
        } catch (Exception e) {
        }
        return true;
    }

}
