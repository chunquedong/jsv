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

public class WhereMaker {
	public static void getSql(StringBuilder sql, Schema table, Object obj) {
		sql.append(" from ").append(table.getName());

		boolean isFirst = true;
		for (int i=0,n=table.size(); i<n; ++i) {
			Field f = table.get(i);
			Object val = f.getValue(obj);
			if (val != null) {
				if (!isFirst) {
					sql.append(" and ");
				} else {
					sql.append(" where ");
				}
				sql.append(f.getName()+"=?");
				isFirst = false;
			}
		}
	}
	
	public static Object[] getParam(Schema table, Object obj)
	{
		int size = 0;
		int count = 0;
		for (int i=0,n=table.size(); i<n; ++i) {
			Field f = table.get(i);
			Object val = f.getValue(obj);
			if (val != null) {
				++size;
			}
		}
		
		Object[] param = new Object[size];
		for (int i=0,n=table.size(); i<n; ++i) {
			Field f = table.get(i);
			Object val = f.getValue(obj);
			if (val != null) {
				param[count] = val;
				++count;
			}
		}
		return param;
	}
}
