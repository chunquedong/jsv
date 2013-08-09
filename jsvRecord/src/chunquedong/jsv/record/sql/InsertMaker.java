//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.sql;

import chunquedong.jsv.record.model.Field;
import chunquedong.jsv.record.model.Schema;

public class InsertMaker {
	public static String getSql(Schema table) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into ").append(table.getName()).append("(");

		for (int i=0,n=table.size(); i<n; ++i) {
			if (table.isAutoGenerateId() && table.getPk().getIndex() == i) {
				//jump
			} else {
				Field f = table.get(i);
				sql.append(f.getName()+",");
			}
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")values(");

		int n = table.size();
		if (table.isAutoGenerateId()) {
			--n;
		}
		for (int i=0; i < n; ++i) {
			sql.append("?,");
		}
		sql.deleteCharAt(sql.length() - 1).append(")");

		return sql.toString();
	}
	
	public static Object[] getParam(Schema table, Object obj)
	{
		int size = table.size();
		if (table.isAutoGenerateId()) {
			--size;
		}
		int count = 0;
		Object[] param = new Object[size];
		for (int i=0,n=table.size(); i<n; ++i) {
			if (table.isAutoGenerateId() && table.getPk().getIndex() == i) {
				//jump
			} else {
				Field f = table.get(i);
				param[count] = f.getValue(obj);
				++count;
			}
		}
		return param;
	}
}
