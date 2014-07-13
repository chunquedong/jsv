package chunquedong.jsv.util;

import java.io.UnsupportedEncodingException;

public class Base64 {
	public static String encode(byte[] bytes) {
		byte[] out = it.sauronsoftware.base64.Base64.encode(bytes);
		try {
			String t = new String(out, "UTF-8");
			return t.replace('+', '_').replace('/', '-').replace('=', '.');
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] decode(String code) {
		byte[] in = null;
		try {
			code = code.replace('_', '+').replace('-', '/').replace('.', '=');
			in = code.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return it.sauronsoftware.base64.Base64.decode(in);
	}
}
