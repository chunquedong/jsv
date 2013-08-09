//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.model;

public class Record {
	private Schema schema;
	private Object[] values;
	
	public Record(Schema schema) {
		this.schema = schema;
		values = new Object[schema.size()];
	}
	
	public Object getId() {
		int i = schema.getPk().getIndex();
		if (i < 0) return null;
		return get(i);
	}
	
	public Object get(int i) {
		return values[i];
	}
	
	public void set(int i, Object val) {
		values[i] = val;
	}
	
	public Object get(String name) {
		int i = schema.getIndex(name);
		return get(i);
	}
	
	public void set(String name, Object val) {
		int i = schema.getIndex(name);
		set(i, val);
	}
	
	public Schema getSchema() {
		return schema;
	}
}
