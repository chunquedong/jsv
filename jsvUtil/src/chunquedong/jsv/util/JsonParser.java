package chunquedong.jsv.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chunquedong.jsv.record.model.DataType;
import chunquedong.jsv.record.model.Field;
import chunquedong.jsv.record.model.Record;
import chunquedong.jsv.record.model.Schema;

import commons.json.Json;

public class JsonParser {
	private Map<String, Schema> map = new HashMap<String, Schema>();
	
	public Map<String, Schema> getMap() {
		return map;
	}
	
	public Object parse(String text, Schema table) {
		Object json = Json.deserialize(text);
		return parseJson(json, table);
	}
	
	private Object parseJson(Object json, Schema table) {
		List<Record> list = new ArrayList<Record>();
		if (json instanceof Map) {
			@SuppressWarnings("unchecked")
      Map<String, Object> obj = (Map<String, Object>)json;
			return parseObj(obj, table);
		} else if (json instanceof List) {
			@SuppressWarnings("unchecked")
      List<Object> array = (List<Object>)json;
			for (int i=0; i<array.size(); ++i) {
				Object obj = parseJson(array.get(i), table);
				if (obj instanceof Record) {
					Record rc = (Record)obj;
					list.add(rc);
				}
			}
		} else {
			return json.toString();
		}
		return list;
	}
	
	private Record parseObj(Map<String, Object> obj, Schema table) {
		if (table == null) {
			String typeName = obj.get("__typeName__").toString();
			table = map.get(typeName);
		}
		Record record = table.newInstance();
		record.init(table);
		for (int i=0; i<table.size(); ++i) {
			Field f = table.get(i);
			String name = f.getName();
			if (!obj.containsKey(name)) {
				continue;
			}
			String val = obj.get(name).toString();
			Object v = DataType.parse(f.getType(), val);
			record.set(f.getName(), v);
		}
		return record;
	}
}
