//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.sql;

import chunquedong.jsv.record.model.Schema;

public class IdWhereMaker {
	public static void getSql(StringBuilder sql, Schema table) {
		sql.append(" from ").append(table.getName()).append(" where ");
		sql.append(table.getName()).append(".").append(table.getPk().getName());
		sql.append("=?");
	}
	
	public static Object[] getParam(Object id)
	{
		Object[] param = new Object[1];
		param[0] = id;
		return param;
	}
}