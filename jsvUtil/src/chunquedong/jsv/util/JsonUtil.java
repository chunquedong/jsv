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

import chunquedong.jsv.record.model.DataType;
import chunquedong.jsv.record.model.Field;
import chunquedong.jsv.record.model.Record;
import chunquedong.jsv.record.model.Schema;

public class JsonUtil {

	public static String toJson(List<Record> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("[\n");
		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				sb.append(",\n");
			}
			Record r = list.get(i);
			sb.append("  ");
			toJson(r, sb);
		}
		sb.append("]");
		return sb.toString();
	}

	public static String toJson(Record r) {
		StringBuilder sb = new StringBuilder();
		toJson(r, sb);
		return sb.toString();
	}

	public static void toJson(Record r, StringBuilder sb) {
		sb.append("{");
		Schema table = r.getSchema();
		sb.append("\"__typeName__\":\"").append(table.getName()).append("\"");
		for (int i = 0; i < table.size(); ++i) {
			if (r.get(i) == null) {
				continue;
			}
			Field f = table.get(i);
			String name = f.getName();
			sb.append(",\"").append(name).append("\":");

//			if (f.getType() == DataType.jbyteArray) {
//				String code = (Base64.encode((byte[]) r.get(i)));
//				sb.append("\"").append(code).append("\"");
//			} else {
//				String val = quote(r.get(i).toString());
//				sb.append(val);
//			}
//			
			Object v = r.get(i);
			String sv = toJson(v);
			sb.append(sv);
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
			if (Record.class.isAssignableFrom(f.getType())) {
				try {
					Record v = (Record) (f.get(r));
					if (v == null)
						continue;
					sb.append(",\"").append(f.getName()).append("\":");
					toJson(v, sb);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else if (List.class.isAssignableFrom(f.getType())) {
				try {
					List<?> list = (List<?>) (f.get(r));
					if (list == null)
						continue;
					sb.append(",\"").append(f.getName()).append("\":");
					sb.append("[\n");
					boolean first = true;
					for (int i = 0; i < list.size(); ++i) {
						Object o = list.get(i);
						if (o instanceof Record) {
							if (!first)
								sb.append(",\n");
							toJson((Record) (o), sb);
							first = false;
						}
					}
					sb.append("]");
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					Object v = f.get(r);
					sb.append(",\"").append(f.getName()).append("\":");
					String sv = toJson(v);
					sb.append(sv);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		sb.append("}");
	}
	
	public static String toJson(Object o) {
		if (o == null) {
			return "null";
		}
		else if (o instanceof String) {
			return quote((String)o);
		}
		else if (o instanceof Integer) {
			return ((Integer)o).toString();
		}
		else if (o instanceof Float) {
			return ((Float)o).toString();
		}
		else if (o instanceof Double) {
			return ((Double)o).toString();
		}
		else if (o instanceof Long) {
			return ((Long)o).toString();
		}
		else if (o instanceof Boolean) {
			return ((Boolean)o).toString();
		}
		else if (o instanceof Byte) {
			return ((Byte)o).toString();
		}
		else if (o instanceof byte[]) {
			String code = (Base64.encode((byte[]) o));
			return "\""+code + "\"";
		}
		else if (o instanceof Date) {
			long t = ((Date)o).getTime();
			return ""+t;
		}
		else if (o instanceof Time) {
			long t = ((Time)o).getTime();
			return ""+t;
		}
		else if (o instanceof Timestamp) {
			long t = ((Timestamp)o).getTime();
			return ""+t;
		}
		else {
			return  "\""+o.toString() + "\"";
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
			case '/':
				sb.append('\\');
				sb.append(c);
				break;
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
