package simperf.result;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simperf.thread.DefaultCallback;
import simperf.thread.MonitorThread;
import simperf.util.SimperfUtil;

/**
 * 将结果向HTTP页面提交请求的默认实现
 * 
 * @author imbugs
 */
public class DefaultHttpWriter extends DefaultCallback {
	private static final Logger logger = LoggerFactory
			.getLogger(DefaultHttpWriter.class);
	private LinkedBlockingQueue<Request> requestQueue = new LinkedBlockingQueue<Request>();
	private String httpUrl;
	private boolean running = true;

	public DefaultHttpWriter(String httpUrl) {
		this.httpUrl = httpUrl;
	}

	class Request {
		protected AtomicInteger tryCount = new AtomicInteger(0);
		protected String url;

		public Request(String url) {
			this.url = url;
		}

		public AtomicInteger getTryCount() {
			return tryCount;
		}

		public String getUrl() {
			return url;
		}

	}

	class HttpGetRequest implements Runnable {
		public void run() {
			int currentTry = 0;
			while (running || requestQueue.size() > 0) {
				try {
					Request request = requestQueue.poll();
					if (null == request) {
						SimperfUtil.sleep(200);
						continue;
					}
					int tryCount = request.getTryCount().getAndIncrement();
					if (tryCount >= 5) {
						continue;
					}
					if (tryCount > currentTry) {
						currentTry = tryCount;
						SimperfUtil.sleep(1000);
					}
					int code = readContentFromGet(request.getUrl());
					if (code != 200) {
						requestQueue.offer(request);
					}
				} catch (Exception e) {
				}
			}

		}
	}

	public void onStart(MonitorThread monitorThread) {
		new Thread(new HttpGetRequest()).start();
		List<String> messages = monitorThread.getMessages();
		for (String string : messages) {
			requestQueue.offer(new Request(assembleUrl(string)));
		}
	}

	public void onMonitor(MonitorThread monitorThread, StatInfo statInfo) {
		requestQueue.offer(new Request(assembleUrl(statInfo, false)));
	}

	public void onExit(MonitorThread monitorThread) {
		StatInfo statInfo = monitorThread.getStatInfo();
		requestQueue.offer(new Request(assembleUrl(statInfo, true)));
		System.out.println("exit");
		running = false;
	}

	public String assembleUrl(String msg) {
		String param = "";
		try {
			param += "message=";
			param += encode(msg);
		} catch (Exception e) {
			logger.error("组装URL失败.", e);
		}
		return httpUrl + "?" + param;
	}

	public String assembleUrl(StatInfo statInfo, boolean summary) {
		StringBuffer param = new StringBuffer();
		try {
			if (null != statInfo) {
				param.append("time=");
				param.append(statInfo.time);
				param.append("&");

				param.append("avgtps=");
				param.append(encode(statInfo.avgTps));
				param.append("&");

				param.append("count=");
				param.append(statInfo.count);
				param.append("&");

				param.append("duration=");
				param.append(statInfo.duration);
				param.append("&");

				param.append("fail=");
				param.append(statInfo.fail);
				param.append("&");

				param.append("ttps=");
				param.append(encode(statInfo.tTps));
				param.append("&");

				param.append("tcount=");
				param.append(statInfo.tCount);
				param.append("&");

				param.append("tduration=");
				param.append(statInfo.tDuration);
				param.append("&");

				param.append("tfail=");
				param.append(statInfo.tFail);
				param.append("&");

				param.append("summary=");
				param.append(summary);
			}
		} catch (Exception e) {
			logger.error("组装URL失败.", e);
		}

		return httpUrl + "?" + param.toString();
	}

	public static int readContentFromGet(String url) {
		int code = -1;
		try {
			URL getUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) getUrl
					.openConnection();
			connection.connect();
			code = connection.getResponseCode();
			connection.disconnect();
		} catch (Exception e) {
			logger.info("提交信息失败 URL=[" + url + "]");
		}
		return code;
	}

	public static String encode(String s) throws UnsupportedEncodingException {
		return URLEncoder.encode(s, "gbk");
	}
}
