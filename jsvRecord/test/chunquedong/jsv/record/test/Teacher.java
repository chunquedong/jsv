package chunquedong.jsv.record.test;

import java.sql.Timestamp;

import chunquedong.jsv.record.model.*;

class Teacher {
	Record r;

	Teacher(Record r) { this.r = r; }

	static Schema getSchema()
	{
		Schema schema = new Schema("Teacher");
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

	Integer getId() { return (Integer)r.get(0); }
	void setId(int val) { r.set(0, val); }
	
	String getName() { return (String)r.get(1); }
	void setName(String val) { r.set(1, val); }
	
	int getAge() { return (Integer)r.get(2); }
	void setAge(int val) { r.set(2, val); }
	
	double getWeight() { return (Double)r.get(3); }
	void setWeight(double val) { r.set(3, val); }
	
	byte[] getImage() { return (byte[])r.get(4); }
	void setImage(byte[] val) { r.set(4, val); }
	
	Timestamp getTime() { return new Timestamp((Long)r.get(5)); }
	void setTime(Timestamp val) { r.set(5, val); }
}
