//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chunquedong.jsv.record.model.*;

public class WebUtil {
	
	public static void fillRecord(HttpServletRequest request, Record record) {
		Schema table = record.getSchema();
		for (int i=0; i<table.size(); ++i) {
			Field f = table.get(i);
			String str = request.getParameter(table.getName() + "," + f.getName());
			if (str == null) continue;
			try {
			  Object val = DataType.parse(f.getType(), str);
			  record.set(i, val);
			} catch (Exception ex) {
				//continue;
			}
		}
	}
	
	public static String getCookieStr(HttpServletRequest request, String name) {
		Cookie c = getCookie(request, name);
		if (c == null) return null;
		return c.getValue();
	}
	
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie[] cs = request.getCookies();
		for (Cookie c : cs) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}
	
	public static void deleteCookie(HttpServletResponse response, String name) {
		Cookie cookie = new Cookie(name, "");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
	
}
