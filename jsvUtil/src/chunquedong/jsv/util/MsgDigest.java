package chunquedong.jsv.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MsgDigest {
	public static String digest(String algorithm, String src) {
		MessageDigest md = null;
    try {
      md = MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    
    md.update(src.getBytes());
    byte[] bs = md.digest();
    
    StringBuffer sb = new StringBuffer();
    for(int i=0; i<bs.length; i++){
			if(bs[i] < 16){
				sb.append(0);
			}
			sb.append(Integer.toHexString(bs[i]));
    }
    return sb.toString();
	}
}
