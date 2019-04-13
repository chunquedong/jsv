//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.sql;

import java.util.ArrayList;
import java.util.List;

import chunquedong.jsv.record.model.Field;
import chunquedong.jsv.record.model.Schema;

public class UpdateMaker {
	public static void getSql(StringBuilder sql, Schema table, Object obj) {
		sql.append("update ").append(table.getName()).append(" set ");

		for (int i=0,n=table.size(); i<n; ++i) {
			if (table.getPk().getIndex() == i) {
				//jump
			} else {
				Field f = table.get(i);
				Object value = f.getValue(obj);
				if (value == null) continue;
				
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
		List<Object> param = new ArrayList<Object>();
		for (int i=0,n=table.size(); i<n; ++i) {
			if (table.getPk().getIndex() == i) {
				//jump
			} else {
				Field f = table.get(i);
				Object value = f.getValue(obj);
				if (value == null) continue;
				
				param.add(value);
				++count;
			}
		}
		Object id = table.getPk().getValue(obj);
		param.add(id);
		return param.toArray();
	}
}
