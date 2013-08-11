//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import chunquedong.jsv.record.connect.DbUtil;
import chunquedong.jsv.record.model.*;

public class SqlUtil {
	private static Object getValue(int i, ResultSet r, int type) throws SQLException {
		switch (type) {
		case DataType.jinteger:
			return r.getInt(i);
		case DataType.jbyte:
			return r.getByte(i);
		case DataType.jshort:
			return r.getShort(i);
		case DataType.jlong:
			return r.getLong(i);
		case DataType.jboolean:
			return r.getBoolean(i);
		case DataType.jstring:
			return r.getString(i);
		case DataType.jbyteArray:
			return r.getBytes(i);
		case DataType.jdate:
			return r.getDate(i);
		case DataType.jtime:
			return r.getTime(i);
		case DataType.jtimestamp:
			return r.getTimestamp(i);
		case DataType.jdecimal:
			return r.getBigDecimal(i);
		case DataType.jdouble:
			return r.getDouble(i);
		case DataType.jfloat:
			return r.getFloat(i);
		case DataType.jother:
			return r.getObject(i);
		default:
			return r.getObject(i);
		}
	}
	
	public static boolean fillToObj(Schema s, Object obj, ResultSet r)
	{
		try {
			for (int i=0; i<s.size(); ++i) {
				Field f = s.get(i);
				Object val;
				val = getValue(i+1, r, f.getType());
				f.setValue(obj, val);
			}
			return true;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean checkMatchDb(Schema s, ResultSet tcols) {
		return true;
	}
	
	public static Schema getTable(Connection conn, String tableName) {
		Schema table = new Schema(tableName);
		ResultSet columns = null;
		try
		{
			DatabaseMetaData meta = conn.getMetaData();
			columns = meta.getColumns(null, null, tableName.toUpperCase(), null);

			int nameIndex = columns.findColumn("COLUMN_NAME");
			//int typeIndex = columns.findColumn("DATA_TYPE");
			int typeNameIndex = columns.findColumn("TYPE_NAME");
			if (columns.next())
			{
				String name = columns.getString(nameIndex);
				String typeName = columns.getString(typeNameIndex);
				Field field = new Field(name, typeName);
				table.add(field);
			}
			else
			{
				return null;
			}
		}
		catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		finally
		{
			DbUtil.colseResultSet(columns);
		}
		return null;
	}
}
