//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.util;

import java.lang.reflect.Modifier;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import chunquedong.jsv.record.model.ArrayRecord;
import chunquedong.jsv.record.model.DataType;
import chunquedong.jsv.record.model.Field;
import chunquedong.jsv.record.model.Record;
import chunquedong.jsv.record.model.Schema;

public class JsonUtil {

	private static void toRecordJson(Record r, StringBuilder sb, int level) {
		sb.append('{');
		indent(sb, level);
		Schema table = r.getSchema();
		sb.append("\"__typeName__\":\"").append(table.getName()).append("\",");
		
		for (int i = 0; i < table.size(); ++i) {
			if (r.get(i) == null) {
				continue;
			}
			indent(sb, level);
			Field f = table.get(i);
			String name = f.getName();
			toJson(name, sb, level+1);
			sb.append(':');
			Object v = r.get(i);
			toJson(v, sb, level+1);
			sb.append(',');
		}

		Class<?> clz = r.getClass();
		java.lang.reflect.Field[] fs = clz.getDeclaredFields();
		for (java.lang.reflect.Field f : fs) {
			if ((f.getModifiers() & Modifier.STATIC) != 0)
				continue;
			if ((f.getModifiers() & Modifier.TRANSIENT) != 0)
				continue;
			if ((f.getModifiers() & Modifier.PRIVATE) != 0)
				continue;
			
			if (clz == ArrayRecord.class && f.getName().equals("values")) {
				continue;
			}
			
			Object val;
			try {
				val = f.get(r);
				if (val == null) continue;
				indent(sb, level);
				toJson(f.getName(), sb, level+1);
				sb.append(':');
				toJson(val, sb, level+1);
				sb.append(',');
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		if (sb.charAt(sb.length()-1) ==',')
			sb.deleteCharAt(sb.length()-1);
		
		indent(sb, level);
		sb.append("}");
	}
	
	public static String toJson(Object o) {
		StringBuilder sb = new StringBuilder();
		toJson(o, sb, 0);
		return sb.toString();
	}
	
	private static void indent(StringBuilder sb, int level) {
		sb.append('\n');
		for (int i=0; i<level; ++i) {
			sb.append("  ");
		}
	}
	
	static void toJson(Object o, StringBuilder sb, int level) {
		
		if (o == null) {
			sb.append("null");
		}
		else if (o instanceof String) {
			sb.append(quote((String)o));
		}
		else if (o instanceof Integer) {
			sb.append( ((Integer)o).toString());
		}
		else if (o instanceof Short) {
			sb.append( ((Short)o).toString());
		}
		else if (o instanceof Float) {
			sb.append( ((Float)o).toString());
		}
		else if (o instanceof Double) {
			sb.append( ((Double)o).toString());
		}
		else if (o instanceof Long) {
			sb.append( ((Long)o).toString());
		}
		else if (o instanceof Boolean) {
			sb.append( ((Boolean)o).toString());
		}
		else if (o instanceof Byte) {
			sb.append( ((Byte)o).toString());
		}
		else if (o instanceof byte[]) {
			String code = (Base64.encode((byte[]) o));
			sb.append( "\""+code + "\"");
		}
		else if (o instanceof Date) {
			long t = ((Date)o).getTime();
			sb.append( ""+t);
		}
		else if (o instanceof Time) {
			long t = ((Time)o).getTime();
			sb.append( ""+t);
		}
		else if (o instanceof Timestamp) {
			long t = ((Timestamp)o).getTime();
			sb.append( ""+t);
		}
		else if (o instanceof Record) {
			toRecordJson((Record)o, sb, level);
		}
		else if (o instanceof Map) {
			boolean first = true;
			sb.append('{');
			indent(sb, level);
			for (Map.Entry e : ((Map<?,?>)o).entrySet()) {
				if (!first) {
					sb.append(',');
				} else {
					first = false;
				}
				indent(sb, level);
				toJson(e.getKey(), sb, level+1);
				sb.append(':');
				toJson(e.getValue(), sb, level+1);
			}
			indent(sb, level);
			sb.append('}');
		}
		else if (o instanceof List) {
			List list = (List)o;
			sb.append('[');
			indent(sb, level);
			for (int i=0; i<list.size(); ++i) {
				if (i>0) {
					sb.append(',');
				}
				indent(sb, level);
				toJson(list.get(i), sb, level+1);
			}
			indent(sb, level);
			sb.append(']');
		}
		else {
			toJson(o.toString(), sb, level+1);
		}
	}

	public static String quote(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		char c = 0;
		int i;
		int len = string.length();
		StringBuilder sb = new StringBuilder(len + 4);
		String t;

		sb.append('"');
		for (i = 0; i < len; i += 1) {
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
//			case '/':
//				sb.append('\\');
//				sb.append(c);
//				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				if (c < ' ') {
					t = "000" + Integer.toHexString(c);
					sb.append("\\u" + t.substring(t.length() - 4));
				} else {
					sb.append(c);
				}
			}
		}
		sb.append('"');
		return sb.toString();
	}
}
