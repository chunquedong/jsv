//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.sql;

import chunquedong.jsv.record.model.*;
import chunquedong.jsv.record.sql.dialect.SqlDialect;

public class TableMaker {
	public static String createTable(Schema table, SqlDialect dialect) {
		StringBuilder sql = new StringBuilder();
		sql.append("create table ").append(table.getName()).append("(");

		for (int i=0,n=table.size(); i<n; ++i) {
			Field f = table.get(i);
			if (i != 0) {
				sql.append(",");
			}
			dialect.createTableColumn(table, f, sql);
		}
		
//		if (table.getPk() != null) {
//			sql.append(", primary key (");
//			sql.append(table.getPk().getName());
//			sql.append(")");
//		}
		sql.append(")");
		return sql.toString();
	}
	
	public static String createIndex(String tableName, String fieldName)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("create index index_");
		sql.append(tableName).append("_").append(fieldName);
		sql.append(" on ").append(tableName);
		sql.append("(").append(fieldName).append(")");
		return sql.toString();
	}

	public static String dropTable(Schema table)
	{
		return "drop table " + table.getName();
	}
}
