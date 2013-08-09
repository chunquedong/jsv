package chunquedong.jsv.record.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import chunquedong.jsv.record.model.Record;
import chunquedong.jsv.record.model.Schema;
import chunquedong.jsv.record.model.SerializeUtil;

public class SerializeTest {
	
	@Test
	public void test() {
		
		Schema table = Teacher.createSchema();
		
		Teacher t1 = new Teacher();
		t1.init(table);
		t1.setName("yjd");
		t1.setAge(26);
		t1.setWeight(56.9f);
		byte[] data = new byte[]{'k'};
		t1.setImage(data);
		t1.setTime(new Timestamp(System.currentTimeMillis()));
		
		List<Record> list = new ArrayList<Record>();
		list.add(t1);
		
	  try {
	  	ByteArrayOutputStream os = new ByteArrayOutputStream();
	  	SerializeUtil.write(os, list);
	  	os.flush();
	  	os.close();
      
      ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
      List<Record> list2 = SerializeUtil.read(is);
      is.close();
      
      Teacher t2 = (Teacher)list2.get(0);
      Schema nt = t2.getSchema();
      System.out.println(t2);
      System.out.println(nt);
      
  		Assert.assertEquals(t2.getAge(), 26);
  		Assert.assertTrue(Math.abs(t2.getWeight()-56.9f) < 1E-5);
  		Assert.assertEquals(t2.getImage()[0], 'k');
  		Assert.assertEquals(t2.get("name"), "yjd");
  		System.out.println(t2.getTime());
      
	  } catch (Exception e) {
	      e.printStackTrace();
	  }
	}
}
