package chunquedong.jsv.record.test;


import java.sql.Timestamp;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import chunquedong.jsv.record.Context;
import chunquedong.jsv.record.connect.ConnectionPool;
import chunquedong.jsv.record.model.Schema;

public class BaseTest {
	
	static Logger log = Logger.getLogger("jsvRecord");
	Context c;
	ConnectionPool connectionPool;
	Schema table;
	
	static {
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		log.addHandler(handler);
	}

	@Before
	public void setup()
	{
		log.setLevel(Level.ALL);
		table = Teacher.createSchema();
		String driver = "org.h2.Driver";
		String url = "jdbc:h2:./test";
		String userName = "postgres";
		String passWord = "111";
		connectionPool = new ConnectionPool(driver, url, userName, passWord, 20);
		Context.setConnection(connectionPool.open());
		c = new Context();
	}

	@After
	public void teardown()
	{
		connectionPool.clearAll();
	}
	
	private void buildTable()
	{
		if (c.tableExists(table))
		{
			c.dropTable(table);
		}
		c.createTable(table);
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
		c.insert(t1);
		Assert.assertNotNull(t1.getId());
	}

	private void query()
	{
		Teacher t1 = new Teacher();
		t1.init(table);
		t1.setName("yjd");
		Teacher t2 = (Teacher)c.one(t1);

		Assert.assertEquals(t2.getAge(), 26);
		Assert.assertTrue(Math.abs(t2.getWeight()-56.9f) < 1E-5);
		Assert.assertEquals(t2.getImage()[0], 'k');
		System.out.println(t2.getTime());
		
		Teacher t3 = new Teacher();
		t3.init(table);
		t3.setSid(t2.getSid());
		c.loadById(table, t3);
		Assert.assertEquals(t3.getAge(), 26);
	}
	
	@Test
	public void test() {
		buildTable();
		insert();
		query();
	}

}
