//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.model;

import java.util.Arrays;
import java.util.Date;

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
		if (i < 0)
			return null;
		return get(i);
	}

	@Override
	public void setId(Object id) {
		int i = schema.getPk().getIndex();
		if (i < 0)
			return;
		set(i, id);
	}

	@Override
	public Object get(int i) {
		return values[i];
	}

	@Override
	public void set(int i, Object val) {
		if (val != null) {
			int type = schema.get(i).getType();
			if (!DataType.isMatchType(type, val)) {
				if (val instanceof Integer) {
					val = DataType.fromInt(type, (Integer) val);
				} else if (val instanceof Long) {
					val = DataType.fromLong(type, (Long) val);
				} else if (val instanceof Float) {
					val = DataType.fromFloat(type, (Float) val);
				} else if (val instanceof Double) {
					val = DataType.fromDouble(type, (Double) val);
				} else if (val instanceof String) {
					val = DataType.fromString(type, (String) val);
				} else if (val instanceof Date) {
					val = DataType.fromDate(type, (Date) val);
				} else {
					throw new RuntimeException("type miss match, expected:"
							+ DataType.getClass(schema.get(i).getType())
							+ ",but:" + val.getClass());
				}
			}
		}
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
		return schema.getName() + ":" + Arrays.toString(values);
	}

	// ////////////////////////////////////////////////////////////////////

	public String getAsString(String name) {
		Object obj = get(name);
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}

	public void setAsString(String name, String val) {
		int i = schema.getIndex(name);
		Field field = schema.get(i);
		Object obj = DataType.fromString(field.getType(), val);
		set(i, obj);
	}

	public long getAsLong(String name) {
		int i = schema.getIndex(name);
		Field field = schema.get(i);
		Object obj = get(i);
		return DataType.toLong(field.getType(), obj);
	}

	public void setAsLong(String name, long val) {
		int i = schema.getIndex(name);
		Field field = schema.get(i);
		Object obj = DataType.fromLong(field.getType(), val);
		set(i, obj);
	}

	public int getAsInt(String name) {
		int i = schema.getIndex(name);
		Field field = schema.get(i);
		Object obj = get(i);
		return DataType.toInt(field.getType(), obj);
	}

	public void setAsInt(String name, int val) {
		int i = schema.getIndex(name);
		Field field = schema.get(i);
		Object obj = DataType.fromInt(field.getType(), val);
		set(i, obj);
	}

	public double getAsDouble(String name) {
		int i = schema.getIndex(name);
		Field field = schema.get(i);
		Object obj = get(i);
		return DataType.toDouble(field.getType(), obj);
	}

	public void setAsDouble(String name, double val) {
		int i = schema.getIndex(name);
		Field field = schema.get(i);
		Object obj = DataType.fromDouble(field.getType(), val);
		set(i, obj);
	}

	public double getAsFloat(String name) {
		int i = schema.getIndex(name);
		Field field = schema.get(i);
		Object obj = get(i);
		return DataType.toDouble(field.getType(), obj);
	}

	public void setAsFloat(String name, double val) {
		int i = schema.getIndex(name);
		Field field = schema.get(i);
		Object obj = DataType.fromDouble(field.getType(), val);
		set(i, obj);
	}

	public java.util.Date getAsDate(String name) {
		int i = schema.getIndex(name);
		Field field = schema.get(i);
		Object obj = get(i);
		return DataType.toDate(field.getType(), obj);
	}

	public void setAsDate(String name, java.util.Date val) {
		int i = schema.getIndex(name);
		Field field = schema.get(i);
		Object obj = DataType.fromDate(field.getType(), val);
		set(i, obj);
	}
	
	public void increaseField(String field) {
		int i = getAsInt(field);
		setAsInt(field, i+1);
	}
	
	public void decreaseField(String field) {
		int i = getAsInt(field);
		--i;
		if (i < 0) i = 0;
		setAsInt(field, i);
	}
}
