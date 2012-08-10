package simperf.sample;

import java.util.Random;

public class MessageSender {

    Random rand = new Random();

    public boolean send() {
        try {
            Thread.sleep(rand.nextInt(10));
        } catch (Exception e) {
        }
        return true;
    }

}
