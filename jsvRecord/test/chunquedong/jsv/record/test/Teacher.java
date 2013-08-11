package chunquedong.jsv.record.test;

import java.sql.Timestamp;

import chunquedong.jsv.record.model.*;

public class Teacher extends ArrayRecord {
  private static final long serialVersionUID = 8025910303754944217L;
  
	static Schema createSchema()
	{
		Schema schema = new Schema("Teacher", Teacher.class);
		schema.add(new Field("sid", DataType.identity));
		schema.add(new Field("name", DataType.character+"(128)"));
		schema.add(new Field("age", DataType.integer));
		schema.add(new Field("weight", DataType.float_));
		schema.add(new Field("image", DataType.blob));
		schema.add(new Field("time", DataType.timestamp));
		
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
	
	float getWeight() { return (Float)super.get(3); }
	void setWeight(float val) { super.set(3, val); }
	
	byte[] getImage() { return (byte[])super.get(4); }
	void setImage(byte[] val) { super.set(4, val); }
	
	Timestamp getTime() { return ((Timestamp)super.get(5)); }
	void setTime(Timestamp val) { super.set(5, val); }
}
