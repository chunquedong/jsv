//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schema {
	private List<Field> columns;
	private Map<String, Integer> map;
	private String name;
	
	private int idIndex = -1;
	private boolean autoGenerateId = false;
	
	public Schema(String name) {
		columns = new ArrayList<Field>();
		map = new HashMap<String, Integer>();
		this.name = name;
	}
	
	public Object newInstance() {
		return new Record(this);
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
	
}
