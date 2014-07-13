package jsvUtil;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import chunquedong.jsv.util.Base64;

public class Base64Test {

	@Test
	public void test() {
		byte[] array = new byte[253];
		for (int i=0; i<253; ++i) {
			array[i] = (byte)i;
		}
		String code = (Base64.encode(array));
		byte[] out = Base64.decode(code);
		Assert.assertTrue(Arrays.equals(array, out));
	}
}
