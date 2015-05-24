package chunquedong.jsv.route;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import chunquedong.jsv.cache.Cache;
import chunquedong.jsv.cache.LruCache;

public class CachedController extends Controller {
	protected Cache cache = new LruCache();

	public Cache getCache() {
		return cache;
	}

	class ServletOutStream extends javax.servlet.ServletOutputStream {
		java.io.ByteArrayOutputStream outStream = new java.io.ByteArrayOutputStream();

		@Override
		public void write(int b) throws IOException {
			outStream.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			outStream.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			outStream.write(b, off, len);
		}

		@Override
		public void close() throws IOException {
			outStream.close();
		}

		@Override
		public void flush() throws IOException {
			outStream.flush();
		}
	}

	class ResponseWrapper extends HttpServletResponseWrapper {

		ServletOutStream out = new ServletOutStream();

		public ResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			PrintWriter writer = new PrintWriter(out);
			return writer;
		}

		@Override
		public ServletOutputStream getOutputStream() throws java.io.IOException {
			ServletOutStream out = new ServletOutStream();
			return out;
		}

		public void flush() throws IOException {
			super.getOutputStream().write(out.outStream.toByteArray());
		}
	}

	@Override
	protected void invoke(Method method, String param) throws IOException {
		Cache cache = getCache();

		ResponseWrapper res = null;
		Cache.CacheItem item = null;
		if (cache != null) {
			item = needCache(method, request.getMethod(),
					request.getRequestURI());
			Object obj = cache.get(item.key);
			if (obj != null) {
				response.addHeader("IS_CACHE", "true");
				this.response.getOutputStream().write((byte[]) obj);
				return;
			}
			
			if (item != null) {
				res = new ResponseWrapper(this.response);
				this.response = res;
			}
		}

		super.invoke(method, param);

		if (res != null) {
			cache.put(item.key, res.out.outStream.toByteArray(), item.expiry);
		}
	}

	private Cache.CacheItem needCache(Method method, String name, String url) {
		if (name.equals("GET")) {
			if (method.isAnnotationPresent(Action.Cache.class)) {
				Cache.CacheItem item = new Cache.CacheItem();
				item.key = url;
				item.expiry = method.getAnnotation(Action.Cache.class).expiry();
				return item;
			}
		}
		return null;
	}
}
