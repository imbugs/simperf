simperf
=======

Simperf 是一个简单的性能测试工具，它提供了一个多线程测试框架

 Example:

 Simperf perf = new Simperf(50, 2000, 1000, 
       new SimperfThreadFactory() {
            public SimperfThread newThread() {
                return new SimperfThread();
            }
       });
 // 设置结果输出文件，默认 simperf-result.log
 perf.getPrintThread().setLogFile("simperf.log");
 // 开始性能测试
 perf.start();
 