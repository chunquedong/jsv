//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.model;

import java.io.Serializable;
import java.util.Arrays;

public class Record implements Serializable {
  private static final long serialVersionUID = -3152163237643045397L;
	private transient Schema schema;
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
	
	public void setSchema(Schema s) {
		this.schema = s;
	}
	
	@Override
	public String toString() {
		return schema.getName() +":"+ Arrays.toString(values);
	}
}
