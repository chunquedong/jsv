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

public class SelectMaker {
	public static String getSql(Schema table) {
		StringBuilder sql = new StringBuilder();
		sql.append("select ");

		for (int i=0,n=table.size(); i<n; ++i) {
			Field f = table.get(i);
			if (i != 0) {
				sql.append(",");
			}
			sql.append(table.getName()).append(".");
			sql.append(f.getName());
		}

		return sql.toString();
	}
}
