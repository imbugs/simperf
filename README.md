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
+ 采样时间间隔
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
结果输出
--------
动态调整
--------
用户自定义
----------
