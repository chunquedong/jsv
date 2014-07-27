package chunquedong.jsv.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {

	/**
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void pipe(InputStream in, OutputStream out) throws IOException {
		byte[] bytes = new byte[1024];
		int len = 0;
		while ((len = in.read(bytes)) != -1) {
			out.write(bytes, 0, len);
		}
	}
}
