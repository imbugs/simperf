package simperf.sample;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import simperf.annotations.AfterInvoke;
import simperf.annotations.AfterRunTask;
import simperf.annotations.BeforeInvoke;
import simperf.annotations.BeforeRunTask;
import simperf.annotations.Simperf;
import simperf.annotations.WarmUp;
import simperf.junit.SimperfTestCase;

public class SimperfTestCaseTest extends SimperfTestCase {
    private SimpleDateFormat sdf;
    private Random           rand;

    @Before
    public void before() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        rand = new Random();
        System.out.println(Thread.currentThread().getId() + "SimperfTestCaseTest.before()");
    }

    @After
    public void after() {
        System.out.println(Thread.currentThread().getId() + "SimperfTestCaseTest.after()");
    }

    @BeforeInvoke
    public void beforeInvoke() {
        System.out.println(Thread.currentThread().getId() + "SimperfTestCaseTest.beforeInvoke()");
    }

    @AfterInvoke
    public void afterInvoke() {
        System.out.println(Thread.currentThread().getId() + "SimperfTestCaseTest.afterInvoke()");
    }

    @BeforeRunTask
    public void beforeRunTask() {
        System.out.println(Thread.currentThread().getId() + "SimperfTestCaseTest.beforeRunTask()");
    }

    @AfterRunTask
    public void afterRunTask() {
        System.out.println(Thread.currentThread().getId() + "SimperfTestCaseTest.afterRunTask()");
    }

    @WarmUp
    public void warmUp() {
        System.out.println(Thread.currentThread().getId() + "SimperfTestCaseTest.warmUp()");
    }

    @Test
    @Simperf(thread = 2, count = 5, interval = 1000)
    public void testXxx() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        sdf.format(new Date());
        boolean result = rand.nextInt(10) > 1;
        System.out.println(Thread.currentThread().getId() + "==================");
        Assert.assertTrue("xxxx", result);
    }
}
