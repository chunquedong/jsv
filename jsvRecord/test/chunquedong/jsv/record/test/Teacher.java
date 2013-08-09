package chunquedong.jsv.record.test;

import java.sql.Timestamp;

import chunquedong.jsv.record.model.*;

public class Teacher extends ArrayRecord {
  private static final long serialVersionUID = 8025910303754944217L;
  
	static Schema createSchema()
	{
		Schema schema = new Schema("Teacher", Teacher.class);
		schema.add(new Field("sid", "integer"));
		schema.add(new Field("name", "string"));
		schema.add(new Field("age", "integer"));
		schema.add(new Field("weight", "float"));
		schema.add(new Field("image", "blob"));
		schema.add(new Field("time", "timestamp"));
		
		schema.setIdIndex(0);
		schema.setAutoGenerateId(true);
		return schema;
	}
	
	Integer getSid() { return (Integer)super.get(0); }
	void setSid(int val) { super.set(0, val); }
	
	String getName() { return (String)super.get(1); }
	void setName(String val) { super.set(1, val); }
	
	int getAge() { return (Integer)super.get(2); }
	void setAge(int val) { super.set(2, val); }
	
	double getWeight() { return (Double)super.get(3); }
	void setWeight(double val) { super.set(3, val); }
	
	byte[] getImage() { return (byte[])super.get(4); }
	void setImage(byte[] val) { super.set(4, val); }
	
	Timestamp getTime() { return new Timestamp((Long)super.get(5)); }
	void setTime(Timestamp val) { super.set(5, val.getTime()); }
}
