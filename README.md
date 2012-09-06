simperf
=======

Simperf 是一个简单的性能测试工具，它提供了一个多线程测试框架

<b>Example:</b>

1. 在代码里使用Simperf
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
<pre>
public class SimperfCommandTest {
	public static void main(String[] args) {
	    SimperfCommand simCommand = new SimperfCommand(args);
	    Simperf perf = simCommand.create();
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
3. 在Junit里使用Simperf
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
