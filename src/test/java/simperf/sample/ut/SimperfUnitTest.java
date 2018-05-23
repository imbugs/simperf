package simperf.sample.ut;

import org.junit.Assert;
import org.junit.Test;
import simperf.util.SimperfUtil;

public class SimperfUnitTest {
    @Test
    public void testX() {
        Assert.assertEquals(SimperfUtil.divide(4, 2), 1);
    }

    @Test
    public void testO() {
        Assert.assertEquals(SimperfUtil.divide(90, 30), "3.00");
    }
}
