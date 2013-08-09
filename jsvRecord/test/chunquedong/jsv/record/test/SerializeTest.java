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
		
		Schema table = Teacher.getSchema();
		
		Record r1 = new Record(table);
		Teacher t1 = new Teacher(r1);
		t1.setName("yjd");
		t1.setAge(26);
		t1.setWeight(56.9f);
		byte[] data = new byte[]{'k'};
		t1.setImage(data);
		t1.setTime(new Timestamp(System.currentTimeMillis()));
		
		List<Record> list = new ArrayList<Record>();
		list.add(r1);
		
	  try {
	  	ByteArrayOutputStream os = new ByteArrayOutputStream();
	  	SerializeUtil.write(os, list);
	  	os.flush();
	  	os.close();
      
      ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
      List<Record> list2 = SerializeUtil.read(is);
      is.close();
      
      Record r2 = list2.get(0);
      Schema nt = r2.getSchema();
      System.out.println(r2);
      System.out.println(nt);
      
  		Teacher t2 = new Teacher(r2);
  		Assert.assertEquals(t2.getAge(), 26);
  		Assert.assertTrue(Math.abs(t2.getWeight()-56.9f) < 1E-5);
  		Assert.assertEquals(t2.getImage()[0], 'k');
  		System.out.println(t2.getTime());
      
	  } catch (Exception e) {
	      e.printStackTrace();
	  }
	}
}
