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
	protected static Cache cache = new LruCache();

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
		private PrintWriter writer = null;

		public ResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			if (writer == null) {
				writer = new PrintWriter(out);
			}
			return writer;
		}

		@Override
		public ServletOutputStream getOutputStream() throws java.io.IOException {
			return out;
		}

		public byte[] flushAndGet() throws IOException {
			if (writer != null) {
				writer.flush();
			} else {
				out.flush();
			}
			
			byte[] result = out.outStream.toByteArray();
			super.getOutputStream().write(result);
			
			if (writer != null) {
				writer.close();
			} else {
				out.close();
			}
			return result;
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
			if (item != null) {
				//try get from cache
				Object obj = cache.get(item.key);
				if (obj != null) {
					response.addHeader("IS_CACHE", "true");
					this.response.getOutputStream().write((byte[]) obj);
					return;
				}
				
				//do wrap
				if (item != null) {
					res = new ResponseWrapper(this.response);
					this.response = res;
				}
			}
		}

		super.invoke(method, param);

		//add to cache
		if (res != null) {
			cache.put(item.key, res.flushAndGet(), item.expiry);
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
