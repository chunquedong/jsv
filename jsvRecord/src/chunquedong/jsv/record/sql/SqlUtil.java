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
	public static Object getInstance(Schema s, ResultSet r)
	{
		Object obj = s.newInstance();
		try {
			for (int i=0; i<s.size(); ++i) {
				Field f = s.get(i);
				Object val;
				val = r.getObject(i+1);
				f.setValue(obj, val);
			}
		} catch (SQLException e) {
			return new RuntimeException(e);
		}
		return obj;
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
