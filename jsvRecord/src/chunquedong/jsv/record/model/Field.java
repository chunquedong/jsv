//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.model;

import java.io.Serializable;

public class Field implements Serializable {
  private static final long serialVersionUID = 9591372409452122L;

	private String name;
	
	private int type;
	private int index = -1;
	private String sqlType;

	private int length = -1;
	private int precision = -1;

	private boolean indexed = false;
	
	public Field(String name, String sqlType) {
		this(name, sqlType, DataType.sqlTypeToJava(sqlType));
	}
	
	public Field(String name, String sqlType, int type) {
		this.name = name;
		this.sqlType	= sqlType;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getSqlType() {
		return sqlType;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public boolean isIndexed() {
		return indexed;
	}

	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}
	
	public Object getValue(Object obj) {
		if (obj instanceof Record) {
			Record r = (Record)obj;
			return r.get(index);
		}
		return null;
	}
	
	public void setValue(Object obj, Object val) {
		if (obj instanceof Record) {
			Record r = (Record)obj;
			
			if (type == DataType.jlong && val instanceof Integer) {
				val = (long)((Integer)val);
			}
			
			r.set(index, val);
		}
	}
	
	@Override
	public String toString() {
		return name + " " + sqlType;
	}
}
