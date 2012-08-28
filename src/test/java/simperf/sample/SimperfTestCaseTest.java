package simperf.sample;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import simperf.junit.Simperf;
import simperf.junit.SimperfTestCase;

public class SimperfTestCaseTest extends SimperfTestCase {
    @BeforeClass
    public static void beforeClass() {
        System.out.println("SimperfTestCaseTest.beforeClass()");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("SimperfTestCaseTest.afterClass()");
    }

    @Before
    public void before() {
        System.out.println("SimperfTestCaseTest.before()");
    }

    @After
    public void after() {
        System.out.println("SimperfTestCaseTest.after()");
    }

    @Test
    @Simperf(thread = 10, count = 100, interval = 1000)
    public void testXxx() {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
        Assert.assertTrue(false);
    }
}
