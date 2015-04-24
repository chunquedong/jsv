package chunquedong.jsv.record.test;


import java.sql.Timestamp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import chunquedong.jsv.record.ConnectionFactory;
import chunquedong.jsv.record.Context;
import chunquedong.jsv.record.model.Schema;

public class BaseTest {
	Schema table;

	@Before
	public void setup()
	{
		table = Teacher.createSchema();
//		String driver = "org.sqlite.JDBC";
//		String url = "jdbc:sqlite:test.db";
		String driver = "org.h2.Driver";
		String url = "jdbc:h2:./test";
		String userName = "postgres";
		String passWord = "111";
		
		ConnectionFactory.getInstance().init(driver, url, userName, passWord, 20, "ALL");
		ConnectionFactory.getInstance().open();
	}
	
	Context cx() {
		return ConnectionFactory.getInstance().getContext();
	}

	@After
	public void teardown()
	{
		ConnectionFactory.getInstance().closeAll();
	}
	
	private void buildTable()
	{
		if (cx().tableExists(table))
		{
			cx().dropTable(table);
		}
		cx().createTable(table);
	}

	private void insert()
	{
		Teacher t1 = new Teacher();
		t1.init(table);
		t1.setName("yjd");
		t1.setAge(26);
		t1.setWeight(56.9f);
		byte[] data = new byte[]{'k'};
		t1.setImage(data);
		t1.setTime(new Timestamp(System.currentTimeMillis()));

		Assert.assertNull(t1.getId());
		cx().insert(t1);
		Assert.assertNotNull(t1.getId());
	}

	private void query()
	{
		Teacher t1 = new Teacher();
		t1.init(table);
		t1.setName("yjd");
		Teacher t2 = (Teacher)cx().one(t1);

		Assert.assertEquals(t2.getAge(), 26);
		Assert.assertTrue(Math.abs(t2.getWeight()-56.9f) < 1E-5);
		Assert.assertEquals(t2.getImage()[0], 'k');
		System.out.println(t2.getTime());
		
		Teacher t3 = new Teacher();
		t3.init(table);
		t3.setSid(t2.getSid());
		cx().loadById(table, t3);
		Assert.assertEquals(t3.getAge(), 26);
	}
	
	private void update() {
		Teacher t1 = new Teacher();
		t1.init(table);
		t1.setName("yjd");
		Teacher t2 = (Teacher)cx().one(t1);
		
		t2.setAge(25);
		cx().update(t2);
		
		Teacher t3 = (Teacher)cx().one(t1);
		Assert.assertEquals(t3.getAge(), 25);
	}
	
	@Test
	public void test() {
		buildTable();
		insert();
		query();
		update();
	}

}
