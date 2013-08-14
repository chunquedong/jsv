//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.model;

import java.io.Serializable;

public interface Record extends Serializable {
	
	public void init(Schema schema);
	
	public Object getId();
	
	public void setId(Object id);
	
	public Object get(int i);
	
	public void set(int i, Object val);
	
	public Object get(String name);
	
	public void set(String name, Object val);
	
	public Schema getSchema();
	
	public void _setSchema(Schema s);
	
}
