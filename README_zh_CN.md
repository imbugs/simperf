### [参考手册](README_zh_CN.md)&nbsp;&nbsp;&nbsp;&nbsp;[Reference manual](README.md)

simperf
=======

Simperf 是一个简单的性能测试工具，它提供了一个多线程测试框架

<b>Example:</b>

1. 在代码里使用Simperf
----------------------
<pre>
Simperf perf = new Simperf(50, 2000, 1000, 
    new SimperfThreadFactory() {
        public SimperfThread newThread() {
            return new SimperfThread();
        }
    });
// 设置结果输出文件，默认 simperf-result.log
perf.getMonitorThread().setLogFile("simperf.log");
// 开始性能测试
perf.start();
</pre>

2. 在命令行里使用Simperf
------------------------
<pre>
public class SimperfCommandTest {
	public static void main(String[] args) {
	    SimperfCommand simCommand = new SimperfCommand(args);
	    Simperf perf = simCommand.create();
		if (perf == null) {
			// 参数解析失败时会返回null
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
执行命令：
<pre>
java SimperfCommandTest -t 10 -c 10 -i 1000
参数说明：
usage: SimperfCommand options
 -c,--count <arg>      [*] number of each thread requests count
 -i,--interval <arg>   [ ] interval of print messages, default 1000
 -j <arg>              [ ] generate jtl report
 -l,--log <arg>        [ ] log filename
 -m,--maxtps <arg>     [ ] max tps
 -t,--thread <arg>     [*] number of thread count
</pre>
3. 在Junit4里使用Simperf
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
        Assert.assertTrue("随机生成结果", result);
    }
}
</pre>

参考手册(v1.0.4)
================
参数的配置
--------
### 基本参数
+ 并发线程数和调用次数
<pre>
        int thread = 10; //并发线程数
        int count = 20;  //每个线程的循环次数
        Simperf perf = new Simperf(thread, count);
		// 启动simperf
</pre>
+ 采样时间间隔(默认1000)
<pre>
        Simperf perf = new Simperf(thread, count);
		int interval = 1000; // 1000ms
        perf.setInterval(interval); //每隔1000ms进行一次采样计算
		// 启动simperf
</pre>
+ 限制最大TPS(可选)
<pre>
        Simperf perf = new Simperf(thread, count);
		int maxTps = 10; //单个线程的最大TPS
        perf.setMaxTps(maxTps);
		// 启动simperf
</pre>

### 命令行参数
在[命令行里使用Simperf](#2-在命令行里使用simperf)中已经给出了框架支持的参数，你可以根据自已的需要来添加支持的参数
<pre>
		SimperfCommand simCommand = new SimperfCommand(args);
        simCommand.getOptions().addOption("a", "argument", true, "一个自定义参数");
        Simperf perf = simCommand.create();
		if (simCommand.getCmd().hasOption("a")) {
            System.out.println(simCommand.getCmd().getOptionValue("a"));
        }
</pre>

结果输出
--------
### 支持输出类型
+ 结果log日志输出
+ 结果文件输出(文件输出可能有缓冲,运行完毕后才全部flush到文件中)
+ jtl文件输出(可提供给jmeter分析)
+ SQL文件输出
+ 自定义输出
框架默认加载了log日志与文件输出两个输出模块，输出格式为Json格式

### 调整输出类型
<pre>
        Simperf perf = new Simperf(10, 10);
        perf.getMonitorThread().clearCallback(); //清除所有callback
        perf.getMonitorThread().registerCallback(new DefaultConsolePrinter()); //添加默认控制台输出(log日志输出模块)
        perf.getMonitorThread().registerCallback(
            new DefaultLogFileWriter(Constant.DEFAULT_RESULT_LOG)); //添加结果文件输出模块,默认文件名为：simperf-result.log
        perf.getMonitorThread().registerCallback(new DefaultSqlFileWriter("xxx.sql"));//添加SQL文件输出
		// 启动simperf
</pre>
jtl文件的配置与其它几种类型配置方式有些不同
<pre>
		Simperf perf = new Simperf(10, 10);
        // 打印JTL日志，会有一些性能损耗
        JTLResult jtl = new JTLResult(perf.getMonitorThread()); //默认输出到Constant.DEFAULT_JTL_FILE
        SimperfConfig.setConfig(SimperfConfig.JTL_RESULT, jtl);
		// 启动simperf
</pre>

### 调整输出位置
+ 文件输出位置
<pre>
		Simperf perf = new Simperf(10, 10);
        perf.getMonitorThread().setLogFile("simperf.log"); //会输出到指定的simperf.log文件中
</pre>
+ 自已添加指定的输出模块
<pre>
        Simperf perf = new Simperf(10, 10);
        perf.getMonitorThread().clearCallback(); //清除所有callback
        perf.getMonitorThread().registerCallback(new DefaultLogFileWriter("simperf.log"));
</pre>

### 自定义输出
定义一个输出模块，继承DefaultCallback基类，然后使用perf.getMonitorThread().registerCallback来加载输出模块
<pre>
    public void onStart(MonitorThread monitorThread); //simperf启动时会调用
    public void onMonitor(MonitorThread monitorThread, StatInfo statInfo); //每次采样计算数据时会调用
    public void onExit(MonitorThread monitorThread); //simperf退出时会调用
</pre>

### 设置输出格式
<pre>
	Constant.DEFAULT_NA = "NaA"; //当出现NA情况时的输出，如0做为除数
    Constant.DEFAULT_DIVIDE_FORMAT = "%.3f"; //精确到3位小数
	// 结果输出格式，默认为Json，需要按顺序补全%s,%s,%d,%d,%d,%s,%d,%d,%d，使用于日志输出与文件输出模块
    Constant.DEFAULT_MSG_FORMAT = ">>>\ntime:%s\navgTps:%s\ncount:%d\nduration:%d\nfail:%d\ntTps:%s\ntCount:%d\ntDuration:%d\ntFail:%d\n";
    Constant.DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss"); //日期输出格式
</pre>

动态调整
--------
+ 运行时调整并发量
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
	// 每隔5s调整一次并发线程数
    public static void thread(Simperf perf, int size) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        perf.thread(size); //动态调整线程数
    }
</pre>
+ 动态调整调用次数
<pre>
	Simperf perf = new Simperf(10, 100);
	// 启动simperf
	perf.count(200); // 运行过程中可以动态调整
</pre>
+ 动态调用监控频率(采样间隔)
<pre>
	Simperf perf = new Simperf(10, 100);
	// 启动simperf
	perf.interval(2000); // 动态调整为2s采样一次
</pre>

+ 步进调整线程
<pre>
		Simperf perf = new Simperf(10, 20);
        TimeSteppingThreadTrigger trigger = new TimeSteppingThreadTrigger(3000, 3); //每3s增加三个新线程
        trigger.setMaxThreads(18); //最大线程数为18
        trigger.startWork(perf); //开始调整
		// 启动simpef
</pre>

SimperfThread
-------------
整个框架是围绕SimperfThread来进行的，SimperfThread是进行具体任务的线程，使用Simperf必须要一个继承了SimperfThread的类，然后实现一个事务操作
<pre>
public class DemoSimperfThread extends SimperfThread {
    // 必选
    public boolean runTask() {
        // 实现一个事务操作
        doSleep();
        // 返回一个结果，true表示该事务成功，false表示该事务失败
        return true;
    }

    /**
     * 睡眠20ms的事务操作
     */
    public void doSleep() {
        try {
            Thread.sleep(20);
        } catch (Exception e) {
        }
    }

    // 可选，预热(阀门打开之前执行，会同步等待其它线程全部执行完毕)
    public void warmUp() {
    }

    // 可选，执行runTask()之前只执行一次，在阀门打开之后执行，线程不同步等待
    public void beforeRunTask() {
    }

    // 可选，执行runTask()之后调用，只执行一次
    public void afterRunTask() {
    }

    // 不建议使用，每次执行runTask()之前调用，SimperfThread中用于收集JTL中的耗时信息
    protected Object beforeInvoke() {
        return System.currentTimeMillis();
    }

    // 不建议使用，每次执行runTask()之后调用，SimperfThread中用于收集JTL中的耗时信息
    protected void afterInvoke(boolean result, Object beforeInvokeResult) {
        long begin = (Long) beforeInvokeResult;
        long end = (Long) System.currentTimeMillis();
        System.out.println("本次调用结果：" + result + ",本次调用耗时：" + (end - begin));
    }
}
</pre>
Junit4的支持
------------
以上特性全部都可以结合Junit4实现
<pre>
public class SimperfTestCaseTest extends SimperfTestCase {
    private static final Logger logger = LoggerFactory.getLogger(SimperfTestCaseTest.class);
    private Random              rand;
    @Before
    public void before() {
        TimeSteppingThreadTrigger t = new TimeSteppingThreadTrigger(2000, 2); //设置步进并发量
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
        boolean result = rand.nextInt(10) > 1; //失败率为10%
        Assert.assertTrue("xxxx", result);
    }
}
</pre>
