//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schema implements Serializable {
  private static final long serialVersionUID = 1404088803985705095L;
	private List<Field> columns;
	private transient Map<String, Integer> map;
	private String name;
	private Class<? extends Record> type;
	
	private int idIndex = -1;
	private boolean autoGenerateId = false;
	
	public Schema(String name, Class<? extends Record> type) {
		columns = new ArrayList<Field>();
		map = new HashMap<String, Integer>();
		this.name = name;
		this.type = type;
	}
	
	public Schema(String name) {
		this(name, ArrayRecord.class);
	}
	
	public void resetMap() {
		if (map == null) {
			map = new HashMap<String, Integer>();
		} else {
			map.clear();
		}
		
		for (Field f : columns) {
			map.put(f.getName(), f.getIndex());
		}
	}
	
	public Record newInstance() {
		Record r = null;
    try {
	    r = (Record)type.newInstance();
    } catch (InstantiationException e) {
	    e.printStackTrace();
    } catch (IllegalAccessException e) {
	    e.printStackTrace();
    }
		r.init(this);
		return r;
	}
	
	public Integer getIndex(String name) {
		return map.get(name);
	}
	
	public void add(Field f) {
		f.setIndex(columns.size());
		columns.add(f);
		map.put(f.getName(), f.getIndex());
	}
	
	public Field get(int i) {
		return columns.get(i);
	}
	
	public int size() {
		return columns.size();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Field getPk() {
		if (idIndex == -1) return null;
		return get(idIndex);
	}

	public void setIdIndex(int idIndex) {
		this.idIndex = idIndex;
	}

	public boolean isAutoGenerateId() {
		return autoGenerateId;
	}

	public void setAutoGenerateId(boolean autoGenerateId) {
		this.autoGenerateId = autoGenerateId;
	}
	
	@Override
	public String toString() {
		return name + ":" + columns;
	}
}
