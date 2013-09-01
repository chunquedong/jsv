package chunquedong.jsv.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HttpClient {
	
	Executor executor;
	
	public HttpClient(int numThread) {
		executor = Executors.newFixedThreadPool(numThread);
	}
	
	public static void initCookie() {
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}
	
	public static String streamToString(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		String line = null;
		StringBuffer sb = new StringBuffer();
		
		while((line=in.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}
	
	public static String get(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		String str = null;
		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			InputStream is = connection.getInputStream();
			str = streamToString(is);
			is.close();
		}
		connection.disconnect();
		return str;
	}
	
	public static String post(String urlStr, String content) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setDoInput(true); 
		connection.setDoOutput(true); 
		connection.setRequestMethod("POST"); 
		connection.setUseCaches(false); 
		connection.setInstanceFollowRedirects(false); 
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		connection.connect();
		BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream()); 
		out.write(content.getBytes("UTF-8"));
		out.flush();
		out.close();
		
		String str = "";
		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			InputStream is = connection.getInputStream();
			str = streamToString(is);
			is.close();
		}
		connection.disconnect();
		return str;
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
        	callback.call(false, e);
        }
      }});
	}
	
	public void asyPost(final String url, final String content, final Callback callback) {
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
        	callback.call(false, e);
        }
      }});
	}
}
