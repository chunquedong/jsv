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

public class HttpClient {

	public static interface HttpHandler {
		void write(OutputStream out) throws IOException;

		void read(InputStream in) throws IOException;

		void onError(int code, Object msg);
	}

	public static class HttpException extends Exception {
		private static final long serialVersionUID = 3369739421484457943L;

		public int responseCode = 0;
		public Object error = null;

		public HttpException(int responseCode) {
			this.responseCode = responseCode;
		}

		public HttpException(int responseCode, String error) {
			super(error);
			this.responseCode = responseCode;
			this.error = error;
		}

		public HttpException(int responseCode, Throwable error) {
			super(error);
			this.responseCode = responseCode;
			this.error = error;
		}

		public static HttpException make(int code, Object error) {
			if (error == null) {
				return new HttpException(code);
			} else if (error instanceof Throwable) {
				return new HttpException(code, (Throwable) error);
			} else {
				return new HttpException(code, error.toString());
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////
	// cookie

	public static void initCookie() {
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}

	public static String getCookie(String uri, String name) {
		CookieManager cookieManager = (CookieManager) CookieHandler
				.getDefault();
		try {
			List<HttpCookie> list = cookieManager.getCookieStore().get(
					new URI(uri));
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
		addCookie(uri, name, value, "/");
	}

	public static void addCookie(String uri, String name, String value, String path) {
		CookieManager cookieManager = (CookieManager) CookieHandler
				.getDefault();
		try {
			URI u = new URI(uri);
			HttpCookie c = new HttpCookie(name, value);
			c.setPath(path);
			c.setDomain(u.getHost());
			cookieManager.getCookieStore().add(new URI(uri), c);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static void clearCookie() {
		CookieManager cookieManager = (CookieManager) CookieHandler
				.getDefault();
		cookieManager.getCookieStore().removeAll();
	}

	// ////////////////////////////////////////////////////////////////////
	// util

	public static String streamToString(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is,
				"UTF-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();

		while ((line = in.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	public static class StringHttpHandler implements HttpHandler {
		public String response = null;
		public String request = null;
		public int responseCode = 0;
		public Object error = null;

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
		public void onError(int code, Object msg) {
			responseCode = code;
			error = msg;
			System.err.println("response code: " + code + ", message:" + msg);
		}
	};

	public static String get(String urlStr) throws HttpException {
		StringHttpHandler handler = new StringHttpHandler();
		handler.request = null;
		doRequest(urlStr, "GET", handler);
		if (handler.responseCode != 0) {
			throw HttpException.make(handler.responseCode, handler.error);
		}
		return handler.response;
	}

	public static String post(String urlStr, String content)
			throws HttpException {
		StringHttpHandler handler = new StringHttpHandler();
		handler.request = content;
		doRequest(urlStr, "POST", handler);
		if (handler.responseCode != 0) {
			throw HttpException.make(handler.responseCode, handler.error);
		}
		return handler.response;
	}

	// ////////////////////////////////////////////////////////////////////
	// stream mode

	public static InputStream getStream(String urlStr) throws HttpException {
		URL url = null;
		try {
			url = new URL(urlStr);

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			InputStream is = connection.getInputStream();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return is;
			} else {
				String s = streamToString(is);
				is.close();
				throw new HttpException(connection.getResponseCode(), s);
			}
		} catch (IOException e) {
			throw new HttpException(-1, e);
		}
	}

	public static InputStream postStream(String urlStr, InputStream in)
			throws HttpException {
		URL url = null;
		try {
			url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.connect();
			OutputStream out = connection.getOutputStream();
			StreamUtil.pipe(in, out);
			out.close();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				String s = null;
				try {
					InputStream is = connection.getInputStream();
					s = streamToString(is);
					is.close();
				} catch (Exception e) {
				}
				throw new HttpException(connection.getResponseCode(), s);
			}

			InputStream is = connection.getInputStream();
			return is;
		} catch (IOException e) {
			throw new HttpException(-1, e);
		}
	}

	// ////////////////////////////////////////////////////////////////////
	// callback mode

	public static void doRequest(String urlStr, String method,
			HttpHandler handler) {
		URL url = null;
		try {
			url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			if (!"GET".equalsIgnoreCase(method)) {
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setUseCaches(false);
				connection.setInstanceFollowRedirects(false);
				connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				connection.connect();
				OutputStream out = connection.getOutputStream();

				try {
					handler.write(out);
					out.flush();
				} finally {
					out.close();
				}
			}

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				String s = null;
				try {
					InputStream is = connection.getInputStream();
					s = streamToString(is);
					is.close();
				} catch (Exception e) {
				}
				handler.onError(connection.getResponseCode(), s);
			} else {
				InputStream is = connection.getInputStream();
				try {
					handler.read(is);
				} finally {
					is.close();
				}
			}
		} catch (IOException e) {
			handler.onError(-1, e);
			e.printStackTrace();
		}
	}
}
