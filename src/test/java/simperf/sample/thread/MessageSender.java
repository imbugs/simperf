package simperf.sample.thread;

import java.util.Random;

public class MessageSender {
    public int sleepTime = 1000;
    Random     rand      = new Random();

    public boolean send() {
        try {
            Thread.sleep(rand.nextInt(20)+50);
        } catch (Exception e) {
        }
        return true;
    }
}
