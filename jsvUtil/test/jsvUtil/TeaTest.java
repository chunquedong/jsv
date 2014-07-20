package jsvUtil;

import org.junit.Assert;
import org.junit.Test;

import chunquedong.jsv.util.Tea;

public class TeaTest {

	@Test
	public void test() {
		String src = "Hello World";
		String secret = Tea.encryptString(src, Tea.KEY);
		String expect = Tea.decryptString(secret, Tea.KEY);

		Assert.assertEquals(src, expect);
	}

}
