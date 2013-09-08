package jsvUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import chunquedong.jsv.record.test.Teacher;
import chunquedong.jsv.util.JsonParser;

public class JsonParserTest {

	@Test
	public void test() {
		String text = "{\"__typeName__\":\"Teacher\",\"name\":\"yjd\",\"age\":\"26\"}";
		JsonParser paser = new JsonParser();
		paser.getMap().put("Teacher", Teacher.createSchema());
		Teacher teacher = (Teacher)paser.parse(text, null);
		Assert.assertEquals(teacher.getAge(), 26);
		Assert.assertEquals(teacher.getName(), "yjd");
	}
	
	@Test
	public void testParseList() {
		String text = "[{\"__typeName__\":\"Teacher\",\"name\":\"yjd\",\"age\":\"26\"}"
				+ ",{\"__typeName__\":\"Teacher\",\"name\":\"yjd2\",\"age\":\"27\"}]";
		JsonParser paser = new JsonParser();
		paser.getMap().put("Teacher", Teacher.createSchema());
		@SuppressWarnings("unchecked")
    List<Teacher> teachers = (List<Teacher>)paser.parse(text, null);
		Assert.assertEquals(teachers.size(), 2);
		Assert.assertEquals(teachers.get(0).getAge(), 26);
		Assert.assertEquals(teachers.get(1).getName(), "yjd2");
	}

}
