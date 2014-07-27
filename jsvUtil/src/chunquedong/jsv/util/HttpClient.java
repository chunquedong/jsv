package chunquedong.jsv.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HttpClient {

	Executor executor;

	public HttpClient(int numThread) {
		executor = Executors.newFixedThreadPool(numThread);
	}

	public static interface HttpHandler extends Callback {
		void write(OutputStream out) throws IOException;

		void read(InputStream in) throws IOException;
	}

	public static void initCookie() {
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}

	public static String getCookie(String uri, String name) {
		CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
		try {
			List<HttpCookie> list = cookieManager.getCookieStore().get(new URI(uri));
			for (HttpCookie c : list) {
				if (c.getName().equals(name)) {
					return c.getValue();
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addCookie(String uri, String name, String value) {
		CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
		try {
			URI u = new URI(uri);
			HttpCookie c = new HttpCookie(name, value);
			c.setPath("/");
			c.setDomain(u.getHost());
			cookieManager.getCookieStore().add(new URI(uri), c);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static String streamToString(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line = null;
		StringBuffer sb = new StringBuffer();

		while ((line = in.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	static class StringHttpHandler implements HttpHandler {
		public String response = null;
		public String request = null;

		public void write(OutputStream out) {
			if (request == null)
				return;
			BufferedOutputStream os = new BufferedOutputStream(out);
			try {
				os.write(request.getBytes("UTF-8"));
				os.flush();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void read(InputStream in) {
			try {
				response = streamToString(in);
			} catch (IOException e) {
			}
		}

		@Override
		public void call(boolean success, Object arg) {
		}
	};

	public static String get(String urlStr) throws IOException {
		StringHttpHandler handler = new StringHttpHandler();
		getStream(urlStr, handler);
		return handler.response;
	}

	public static void getStream(String urlStr, HttpHandler handler)
	    throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			InputStream is = connection.getInputStream();
			handler.read(is);
			is.close();
		} else {
			handler.call(false, null);
		}
		connection.disconnect();
	}

	public static String post(String urlStr, String content) throws IOException {
		StringHttpHandler handler = new StringHttpHandler();
		handler.request = content;
		postStream(urlStr, handler);
		return handler.response;
	}

	public static void postStream(String urlStr, HttpHandler handler)
	    throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestProperty("Content-Type",
		    "application/x-www-form-urlencoded");
		connection.connect();
		OutputStream out = connection.getOutputStream();
		handler.write(out);
		out.flush();
		out.close();

		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			InputStream is = connection.getInputStream();
			handler.read(is);
			is.close();
		} else {
			handler.call(false, null);
		}
		connection.disconnect();
	}

	public void asyGetStream(final String url, final HttpHandler handler) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					getStream(url, handler);
				} catch (Throwable e) {
					handler.call(false, null);
				}
			}
		});
	}

	public void asyPostStream(final String url, final HttpHandler handler) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					postStream(url, handler);
				} catch (Throwable e) {
					handler.call(false, null);
				}
			}
		});
	}

	public void asyGet(final String url, final Callback callback) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String s = get(url);
					if (s != null) {
						callback.call(true, s);
					} else {
						callback.call(false, null);
					}
				} catch (Throwable e) {
					e.printStackTrace();
					callback.call(false, e);
				}
			}
		});
	}

	public void asyPost(final String url, final String content,
	    final Callback callback) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String s = post(url, content);
					if (s != null) {
						callback.call(true, s);
					} else {
						callback.call(false, null);
					}
				} catch (Throwable e) {
					e.printStackTrace();
					callback.call(false, e);
				}
			}
		});
	}
}
