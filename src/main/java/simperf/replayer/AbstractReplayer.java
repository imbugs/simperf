package simperf.replayer;

import java.util.concurrent.Semaphore;

public abstract class AbstractReplayer extends Semaphore {
    private static final long serialVersionUID = -3198358114016132463L;

    public AbstractReplayer() {
        this(0);
    }

    public AbstractReplayer(int permits) {
        super(permits);
    }
}
