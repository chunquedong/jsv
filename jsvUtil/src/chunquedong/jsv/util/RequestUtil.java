//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.util;

import javax.servlet.http.HttpServletRequest;

import chunquedong.jsv.record.model.*;

public class RequestUtil {
	
	void fillRecord(HttpServletRequest request, Record record) {
		Schema table = record.getSchema();
		for (int i=0; i<table.size(); ++i) {
			Field f = table.get(i);
			String str = request.getParameter(table.getName() + "." + f.getName());
			if (str == null) continue;
			try {
			  Object val = DataType.parse(f.getType(), str);
			  record.set(i, val);
			} catch (Exception ex) {
				//continue;
			}
		}
	}
	
}
