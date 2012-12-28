### [参考手册](README_zh_CN.md)&nbsp;&nbsp;&nbsp;&nbsp;[Reference manual](README.md)

simperf
=======

Simperf is a simple performance test framework for java. It provides a multithread test framework.

<b>Example:</b>

1. Use simperf in java:
----------------------
<pre>
Simperf perf = new Simperf(50, 2000, 1000, 
    new SimperfThreadFactory() {
        public SimperfThread newThread() {
            return new SimperfThread();
        }
    });
// setting output file path，simperf-result.log by default.
perf.getMonitorThread().setLogFile("simperf.log");
// begin performance testing
perf.start();
</pre>

2. Use simperf in command line
------------------------
<pre>
public class SimperfCommandTest {
	public static void main(String[] args) {
	    SimperfCommand simCommand = new SimperfCommand(args);
	    Simperf perf = simCommand.create();
		if (perf == null) {
			// fail to parse the args
			System.exit(-1);
		}
	    perf.start(new SimperfThreadFactory() {
	        public SimperfThread newThread() {
	            return new SimperfThread();
	        }
	    });
	}
}
</pre>
execute command：
<pre>
java SimperfCommandTest -t 10 -c 10 -i 1000
DESCRIPTION :
usage: SimperfCommand options
 -c,--count <arg>      [*] number of each thread requests count
 -i,--interval <arg>   [ ] interval of print messages, default 1000
 -j <arg>              [ ] generate jtl report
 -l,--log <arg>        [ ] log filename
 -m,--maxtps <arg>     [ ] max tps
 -t,--thread <arg>     [*] number of thread count
</pre>
3. Use simperf in JUnit4
------------------------
<pre>
public class SimperfTestCaseTest extends SimperfTestCase {
    private Random              rand;
    @Test
    @Simperf(thread = 2, count = 5, interval = 1000)
    public void testXxx() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        boolean result = rand.nextInt(10) > 1;
        Assert.assertTrue("random result", result);
    }
}
</pre>

Reference manual(v1.0.4)
================
Settings
--------
### Basic Parameters
+ The Number Of Concurrent Threads 
<pre>
        int thread = 10; // the number of threads
        int count = 20;  // the number of times to execute the test for each thread
        Simperf perf = new Simperf(thread, count);
		// start simperf
</pre>
+ Interval (default:1000)
<pre>
        Simperf perf = new Simperf(thread, count);
		int interval = 1000; // 1000ms
        perf.setInterval(interval); // Sampling calculation once every 1000 ms
		// start simperf
</pre>
+ The Maximum TPS (optional)
<pre>
        Simperf perf = new Simperf(thread, count);
		int maxTps = 10; // the maximum TPS for each thread
        perf.setMaxTps(maxTps);
		// start simperf
</pre>

### The Options Of Command Line
[Use simperf in command line](#2-use-simperf-in-command-line) has given the supported options by Simperf. You could add your own options if you need.
<pre>
		SimperfCommand simCommand = new SimperfCommand(args);
        simCommand.getOptions().addOption("a", "argument", true, "a custom argument");
        Simperf perf = simCommand.create();
		if (simCommand.getCmd().hasOption("a")) {
            System.out.println(simCommand.getCmd().getOptionValue("a"));
        }
</pre>

The Output Result
--------
### support types
+ log results by log4j
+ write the results into a file (It will flush the cache after complete)
+ jtl file, which analyzed by jmeter
+ SQL
+ custom types
Simperf registers log4j and file writer by default, and it will take JSON as the default format.

### Change The Output Format
<pre>
        Simperf perf = new Simperf(10, 10);
        perf.getMonitorThread().clearCallback(); //clear all callback
        perf.getMonitorThread().registerCallback(new DefaultConsolePrinter()); //register DefaultConsolePrinter, which uses log4j
        perf.getMonitorThread().registerCallback(
            new DefaultLogFileWriter(Constant.DEFAULT_RESULT_LOG)); //register DefaultLogFileWriter to write file ,default file name：simperf-result.log
        perf.getMonitorThread().registerCallback(new DefaultSqlFileWriter("xxx.sql"));//register DefaultSqlFileWriter, write Sql to file
		// start simperf
</pre>
There are some different to use JTL.
<pre>
		Simperf perf = new Simperf(10, 10);
        // print JTL record
        JTLResult jtl = new JTLResult(perf.getMonitorThread()); //default file: Constant.DEFAULT_JTL_FILE
        SimperfConfig.setConfig(SimperfConfig.JTL_RESULT, jtl);
		// start simperf
</pre>

### Change The Output Location/File
+ Set the filename for writer
<pre>
		Simperf perf = new Simperf(10, 10);
        perf.getMonitorThread().setLogFile("simperf.log"); //FileWriter will write data into simperf.log
</pre>
+ Register callbacks by yourself
<pre>
        Simperf perf = new Simperf(10, 10);
        perf.getMonitorThread().clearCallback(); //clear all callbacks
        perf.getMonitorThread().registerCallback(new DefaultLogFileWriter("simperf.log"));
</pre>

### Custom Output Callback
Create a class extends DefaultCallback，and then call method perf.getMonitorThread().registerCallback() to register callback objects.
<pre>
    public void onStart(MonitorThread monitorThread); // call on start test
    public void onMonitor(MonitorThread monitorThread, StatInfo statInfo); // call on calculate the output
    public void onExit(MonitorThread monitorThread); //call on simperf exit
</pre>

### Setting Output Format
<pre>
	Constant.DEFAULT_NA = "NaA"; //output when NA，0 as a divisor for example
    Constant.DEFAULT_DIVIDE_FORMAT = "%.3f"; //round number to three decimal places
	// output format which use to log or write results，Json by default，the format contains %s,%s,%d,%d,%d,%s,%d,%d,%d
    Constant.DEFAULT_MSG_FORMAT = ">>>\ntime:%s\navgTps:%s\ncount:%d\nduration:%d\nfail:%d\ntTps:%s\ntCount:%d\ntDuration:%d\ntFail:%d\n";
    Constant.DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss"); // default data format
</pre>

Dynamic Adjustment
--------
+ Change the number of concurrent threads when testing
<pre>
    public static void main(String[] args) {
        Simperf perf = new Simperf(10, 100);
        perf.start(new SimperfThreadFactory() {
            public SimperfThread newThread() {
                return new SendMessageThread();
            }
        });
        thread(perf, 7);
        thread(perf, 10);
        thread(perf, 15);
        thread(perf, 20);
        thread(perf, 15);
        thread(perf, 25);
        thread(perf, 10);
        thread(perf, 5);
    }
	// Change the number of concurrent threads every 5s
    public static void thread(Simperf perf, int size) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        perf.thread(size); // dynamic adjustment
    }
</pre>
+ Change the number of times to execute for each thread when testing
<pre>
	Simperf perf = new Simperf(10, 100);
	// start simperf
	perf.count(200); // dynamic adjustment
</pre>
+ Change the interval of print results when testing
<pre>
	Simperf perf = new Simperf(10, 100);
	// start simperf
	perf.interval(2000); // dynamic adjustment
</pre>

+ Stepping change the number of concurrent thread 
<pre>
		Simperf perf = new Simperf(10, 20);
        TimeSteppingThreadTrigger trigger = new TimeSteppingThreadTrigger(3000, 3); //add three new threads every 3s
        trigger.setMaxThreads(18); //the maximum number of threads is 18
        trigger.startWork(perf); // start work
		// start simpef
</pre>

SimperfThread
-------------
SimperfThread is the most important class in Simperf，which implements the specific tasks. You must extend and implement SimperfThread in Simperf for your transactions.
<pre>
public class DemoSimperfThread extends SimperfThread {
    // Required
    public boolean runTask() {
        // implement a transaction operate
        doSleep();
        // return the result, true if success otherwise false
        return true;
    }

    /**
     * Sample task, sleep 20ms
     */
    public void doSleep() {
        try {
            Thread.sleep(20);
        } catch (Exception e) {
        }
    }

    // Optional
    public void warmUp() {
    }

	// Optional
    public void beforeRunTask() {
    }

	// Optional
    public void afterRunTask() {
    }

	// Optional
    protected Object beforeInvoke() {
        return System.currentTimeMillis();
    }

	// Optional
    protected void afterInvoke(boolean result, Object beforeInvokeResult) {
        long begin = (Long) beforeInvokeResult;
        long end = (Long) System.currentTimeMillis();
        System.out.println("result：" + result + ",duration：" + (end - begin));
    }
}
</pre>
Support For Junit4
------------
All of the futures are available in Junit 4
<pre>
public class SimperfTestCaseTest extends SimperfTestCase {
    private static final Logger logger = LoggerFactory.getLogger(SimperfTestCaseTest.class);
    private Random              rand;
    @Before
    public void before() {
        TimeSteppingThreadTrigger t = new TimeSteppingThreadTrigger(2000, 2); // set a stepping thread concurent trigger
        t.startWork(simperf);
        rand = new Random();
        logger.debug(Thread.currentThread().getId() + "SimperfTestCaseTest.before()");
    }

    @After
    public void after() {
        logger.debug(Thread.currentThread().getId() + "SimperfTestCaseTest.after()");
    }

    @BeforeInvoke
    public void beforeInvoke() {
        logger.debug(Thread.currentThread().getId() + "SimperfTestCaseTest.beforeInvoke()");
    }

    @AfterInvoke
    public void afterInvoke() {
        logger.debug(Thread.currentThread().getId() + "SimperfTestCaseTest.afterInvoke()");
    }

    @BeforeRunTask
    public void beforeRunTask() {
        logger.debug(Thread.currentThread().getId() + "SimperfTestCaseTest.beforeRunTask()");
    }

    @AfterRunTask
    public void afterRunTask() {
        logger.debug(Thread.currentThread().getId() + "SimperfTestCaseTest.afterRunTask()");
    }

    @WarmUp
    public void warmUp() {
        logger.debug(Thread.currentThread().getId() + "SimperfTestCaseTest.warmUp()");
    }

    @Test
    @Simperf(thread = 2, count = 5, interval = 1000)
    public void testXxx() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        boolean result = rand.nextInt(10) > 1; // 10% failed
        Assert.assertTrue("xxxx", result);
    }
}
</pre>
