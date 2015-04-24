//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;


public class DataType {
	//sql type
	public final static String integer = "integer";
	public final static String tinyint = "tinyint";
	public final static String smallint = "smallint";
	public final static String bigint = "bigint";
	public final static String bool = "boolean";
	public final static String character = "char";
	public final static String varchar = "varchar";
	public final static String text = "text";
	public final static String blob = "blob";
	public final static String identity = "identity";
	public final static String date = "date";
	public final static String time = "time";
	public final static String timestamp = "timestamp";
	public final static String decimal = "decimal";
	public final static String double_ = "double";
	public final static String float_ = "float";
	
	//java type
	public final static int jinteger = 0;
	public final static int jbyte = 1;
	public final static int jshort = 2;
	public final static int jlong = 3;
	public final static int jboolean = 4;
	public final static int jstring = 5;
	public final static int jbyteArray = 6;
	public final static int jdate = 7;
	public final static int jtime = 8;
	public final static int jtimestamp = 9;
	public final static int jdecimal = 10;
	public final static int jdouble = 11;
	public final static int jfloat = 12;
	public final static int jother = 13;
	
	public static int sqlTypeToJava(String sqlType) {
		if (sqlType.equals(integer)) {
			return jinteger;
		} else if (sqlType.equals(tinyint)) {
			return jbyte;
		} else if (sqlType.equals(smallint)) {
			return jshort;
		} else if (sqlType.equals(bigint)) {
			return jlong;
		} else if (sqlType.equals(bool)) {
			return jboolean;
		} else if (sqlType.startsWith(character)) {
			return jstring;
		} else if (sqlType.startsWith(varchar)) {
			return jstring;
		} else if (sqlType.equals(text)) {
			return jstring;
		} else if (sqlType.equals(blob)) {
			return jbyteArray;
		} else if (sqlType.equals(identity)) {
			return jlong;
		} else if (sqlType.equals(date)) {
			return jdate;
		} else if (sqlType.equals(time)) {
			return jtime;
		} else if (sqlType.equals(timestamp)) {
			return jtimestamp;
		} else if (sqlType.equals(decimal)) {
			return jdecimal;
		} else if (sqlType.equals(double_)) {
			return jdouble;
		} else if (sqlType.equals(float_)) {
			return jfloat;
		} else {
			return jother;
		}
	}
	
	public static Class<?> getClass(int type) {
		switch (type) {
		case DataType.jinteger:
			return Integer.class;
		case DataType.jbyte:
			return Byte.class;
		case DataType.jshort:
			return Short.class;
		case DataType.jlong:
			return Long.class;
		case DataType.jboolean:
			return Boolean.class;
		case DataType.jstring:
			return String.class;
		case DataType.jbyteArray:
			return byte[].class;
		case DataType.jdate:
			return Date.class;
		case DataType.jtime:
			return Time.class;
		case DataType.jtimestamp:
			return Timestamp.class;
		case DataType.jdecimal:
			return BigDecimal.class;
		case DataType.jdouble:
			return Double.class;
		case DataType.jfloat:
			return Float.class;
		case DataType.jother:
			return Object.class;
		default:
			return Object.class;
		}
	}
	
	public static String toString(int type, Object obj) {
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}
	
	public static Object fromString(int type, String s) {
		switch (type) {
		case DataType.jinteger:
			return Integer.parseInt(s);
		case DataType.jbyte:
			return Byte.parseByte(s);
		case DataType.jshort:
			return Short.parseShort(s);
		case DataType.jlong:
			return Long.parseLong(s);
		case DataType.jboolean:
			return Boolean.parseBoolean(s);
		case DataType.jstring:
			return s;
		case DataType.jbyteArray:
			//return Base64.decode(s);
			throw new RuntimeException("TODO");
		case DataType.jdate:
			return Date.valueOf(s);
		case DataType.jtime:
			return Time.valueOf(s);
		case DataType.jtimestamp:
			return Timestamp.valueOf(s);
		case DataType.jdecimal:
			return BigDecimal.valueOf(Double.parseDouble(s));
		case DataType.jdouble:
			return Double.parseDouble(s);
		case DataType.jfloat:
			return Float.parseFloat(s);
		case DataType.jother:
			throw new RuntimeException("TODO");
		default:
			throw new RuntimeException("TODO");
		}
	}
	
	public static long toLong(int type, Object obj) {
		if (obj == null) {
			return 0;
		}
		
		switch (type) {
		case DataType.jinteger:
			return (Integer)obj;
		case DataType.jbyte:
			return (Byte)obj;
		case DataType.jshort:
			return (Short)obj;
		case DataType.jlong:
			return (Long)obj;
		case DataType.jboolean:
			return ((Boolean)obj)?1:0;
		case DataType.jstring:
			return Long.parseLong((String)obj);
		case DataType.jbyteArray:
			break;
		case DataType.jdate:
			return ((Date)obj).getTime();
		case DataType.jtime:
			return ((Time)obj).getTime();
		case DataType.jtimestamp:
			return ((Timestamp)obj).getTime();
		case DataType.jdecimal:
			return ((BigDecimal)obj).longValue();
		case DataType.jdouble:
			return ((Double)obj).longValue();
		case DataType.jfloat:
			return ((Float)obj).longValue();
		case DataType.jother:
			break;
		default:
			break;
		}
		throw new RuntimeException("unsupport");
	}
	
	public static Object fromLong(int type, long s) {
		switch (type) {
		case DataType.jinteger:
			return Integer.valueOf((int) s);
		case DataType.jbyte:
			return Byte.valueOf((byte)s);
		case DataType.jshort:
			return Short.valueOf((short)s);
		case DataType.jlong:
			return Long.valueOf(s);
		case DataType.jboolean:
			return Boolean.valueOf(s != 0);
		case DataType.jstring:
			return (""+s);
		case DataType.jbyteArray:
			//return Base64.decode(s);
			throw new RuntimeException("TODO");
		case DataType.jdate:
			return new Date(s);
		case DataType.jtime:
			return new Time(s);
		case DataType.jtimestamp:
			return new Timestamp(s);
		case DataType.jdecimal:
			return BigDecimal.valueOf(s);
		case DataType.jdouble:
			return Double.valueOf(s);
		case DataType.jfloat:
			return Float.valueOf(s);
		case DataType.jother:
			throw new RuntimeException("TODO");
		default:
			throw new RuntimeException("TODO");
		}
	}
	
	public static double toDouble(int type, Object obj) {
		if (obj == null) {
			return 0;
		}
		
		switch (type) {
		case DataType.jinteger:
			return (Integer)obj;
		case DataType.jbyte:
			return (Byte)obj;
		case DataType.jshort:
			return (Short)obj;
		case DataType.jlong:
			return (Long)obj;
		case DataType.jboolean:
			return ((Boolean)obj)?1:0;
		case DataType.jstring:
			return Long.parseLong((String)obj);
		case DataType.jbyteArray:
			break;
		case DataType.jdate:
			return ((Date)obj).getTime();
		case DataType.jtime:
			return ((Time)obj).getTime();
		case DataType.jtimestamp:
			return ((Timestamp)obj).getNanos();
		case DataType.jdecimal:
			return ((BigDecimal)obj).longValue();
		case DataType.jdouble:
			return ((Double)obj);
		case DataType.jfloat:
			return ((Float)obj).doubleValue();
		case DataType.jother:
			break;
		default:
			break;
		}
		throw new RuntimeException("unsupport");
	}
	
	public static Object fromDouble(int type, double s) {
		switch (type) {
		case DataType.jinteger:
			return Integer.valueOf((int) s);
		case DataType.jbyte:
			return Byte.valueOf((byte)s);
		case DataType.jshort:
			return Short.valueOf((short)s);
		case DataType.jlong:
			return Long.valueOf((long)s);
		case DataType.jboolean:
			return Boolean.valueOf(s != 0);
		case DataType.jstring:
			return (""+s);
		case DataType.jbyteArray:
			//return Base64.decode(s);
			throw new RuntimeException("TODO");
		case DataType.jdate:
			return new Date((long)s);
		case DataType.jtime:
			return new Time((long)s);
		case DataType.jtimestamp:
			return new Timestamp((long)s);
		case DataType.jdecimal:
			return BigDecimal.valueOf(s);
		case DataType.jdouble:
			return Double.valueOf(s);
		case DataType.jfloat:
			return Float.valueOf((float)s);
		case DataType.jother:
			throw new RuntimeException("TODO");
		default:
			throw new RuntimeException("TODO");
		}
	}
}
