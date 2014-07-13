//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.util;

import java.util.List;

import chunquedong.jsv.record.model.DataType;
import chunquedong.jsv.record.model.Field;
import chunquedong.jsv.record.model.Record;
import chunquedong.jsv.record.model.Schema;

public class JsonUtil {
	
	public static String toJson(List<Record> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("[\n");
		for (Record r : list) {
			sb.append("  ");
			toJson(r, sb);
			sb.append(",\n");
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
		for (int i=0; i<table.size(); ++i) {
			if (r.get(i) == null) {
				continue;
			}
			Field f = table.get(i);
			String name = f.getName();
			sb.append(",\"").append(name).append("\":");
			
			if (f.getType() == DataType.jbyteArray) {
				String code = (Base64.encode((byte[])r.get(i)));
				sb.append("\"").append(code).append("\"");
			} else {
				String val = quote(r.get(i).toString());
				sb.append(val);
			}
		}
		sb.append("}");
	}
	
	public static String quote(String string) {
    if (string == null || string.length() == 0) {
        return "\"\"";
    }

    char         c = 0;
    int          i;
    int          len = string.length();
    StringBuilder sb = new StringBuilder(len + 4);
    String       t;

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
