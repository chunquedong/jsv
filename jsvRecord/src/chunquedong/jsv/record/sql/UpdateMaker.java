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

public class UpdateMaker {
	public static void getSql(StringBuilder sql, Schema table) {
		sql.append("update ").append(table.getName()).append(" set ");

		for (int i=0,n=table.size(); i<n; ++i) {
			if (table.getPk().getIndex() == i) {
				//jump
			} else {
				Field f = table.get(i);
				sql.append(f.getName()+"=?,");
			}
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" where ").append(table.getPk().getName()).append("=?");

	}
	
	public static Object[] getParam(Schema table, Object obj)
	{
		int size = table.size();
		int count = 0;
		Object[] param = new Object[size];
		for (int i=0,n=table.size(); i<n; ++i) {
			if (table.getPk().getIndex() == i) {
				//jump
			} else {
				Field f = table.get(i);
				param[count] = f.getValue(obj);
				++count;
			}
		}
		param[count] = table.getPk().getValue(obj);
		return param;
	}
}
