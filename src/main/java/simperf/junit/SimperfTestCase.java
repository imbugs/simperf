package simperf.junit;

import org.junit.runner.RunWith;

import simperf.Simperf;
import simperf.annotations.Inject;

/**
 * Simperf与JUnit集成基类
 * @author imbugs
 */
@RunWith(SimperfJUnit4Runner.class)
public class SimperfTestCase {
    @Inject
    protected Simperf simperf;
}
