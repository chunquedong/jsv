//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.model;

import java.util.Arrays;

public class ArrayRecord implements Record {
  private static final long serialVersionUID = -3152163237643045397L;
	private transient Schema schema;
	public Object[] values;
	
	public ArrayRecord() {
	}
	
	public ArrayRecord(Schema schema) {
		init(schema);
	}
	
	@Override
	public void init(Schema schema) {
		this.schema = schema;
		values = new Object[schema.size()];
	}
	
	@Override
	public Object getId() {
		int i = schema.getPk().getIndex();
		if (i < 0) return null;
		return get(i);
	}
	
	@Override
	public Object get(int i) {
		return values[i];
	}
	
	@Override
	public void set(int i, Object val) {
		values[i] = val;
	}
	
	@Override
	public Object get(String name) {
		int i = schema.getIndex(name);
		return get(i);
	}
	
	@Override
	public void set(String name, Object val) {
		int i = schema.getIndex(name);
		set(i, val);
	}
	
	@Override
	public Schema getSchema() {
		return schema;
	}
	
	@Override
	public void _setSchema(Schema s) {
		this.schema = s;
	}
	
	@Override
	public String toString() {
		return schema.getName() +":"+ Arrays.toString(values);
	}
}
