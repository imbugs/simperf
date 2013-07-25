package simperf.thread;

import simperf.Simperf;

/**
 * ¿ØÖÆÏß³Ì
 * @author imbugs
 */
public abstract class ControllThread extends Thread {
    protected Simperf simperf;

    public ControllThread() {
    }

    public ControllThread(Simperf simperf) {
        this.simperf = simperf;
    }

    public Simperf getSimperf() {
        return simperf;
    }

    public void setSimperf(Simperf simperf) {
        this.simperf = simperf;
    }

}
