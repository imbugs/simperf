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
在[命令行里使用Simperf](#2-在命令行里使用Simperf)中已经给出了框架支持的参数，你可以根据自已的需要来添加支持的参数
<pre>
		SimperfCommand simCommand = new SimperfCommand(args);
        simCommand.getOptions().addOption("a", "argument", true, "一个自定义参数");
        Simperf perf = simCommand.create();
</pre>

结果输出
--------
### 支持输出类型
+ 结果log日志输出
+ 结果文件输出
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
+ 框架默认加载的log日志位置
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

动态调整
--------
自定义模块
--------------
