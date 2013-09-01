package chunquedong.jsv.util;

import java.util.ArrayList;
import java.util.List;

import chunquedong.jsv.record.model.Field;
import chunquedong.jsv.record.model.Record;
import chunquedong.jsv.record.model.Schema;

import commons.json.Json;
import commons.json.JsonArray;
import commons.json.JsonObject;

public class JsonParser {
	public static Object parse(String text, Schema table) {
		Object json = Json.deserialize(text);
		return parseJson(json, table);
	}
	
	private static Object parseJson(Object json, Schema table) {
		List<Record> list = new ArrayList<Record>();
		if (json instanceof JsonObject) {
			JsonObject obj = (JsonObject)json;
			return parseObj(obj, table);
		} else if (json instanceof JsonArray) {
			JsonArray array = (JsonArray)json;
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
	
	private static Record parseObj(JsonObject obj, Schema table) {
		Record record = table.newInstance();
		record.init(table);
		for (int i=0; i<table.size(); ++i) {
			Field f = table.get(i);
			String name = f.getName();
			String val = obj.getAsJsonString(name).toString();
			record.set(f.getName(), val);
		}
		return record;
	}
}
