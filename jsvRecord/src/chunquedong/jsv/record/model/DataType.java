//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.model;



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
	
}
