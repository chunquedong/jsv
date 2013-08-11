//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.route;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class RouteServlet extends HttpServlet {
	
	private static final long serialVersionUID = -7749777676921475430L;
	private String anctionPackage = "";
	
	public String getAnctionPackage() {
		return anctionPackage;
	}

	public void setAnctionPackage(String anctionPackage) {
		this.anctionPackage = anctionPackage;
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String path = request.getRequestURI();
		
		String className = "Index";

		String[] pathList = new String[4];
		int count = splitPath(path, pathList);
		if (count > 0) {
			className = pathList[0];
		}
		
		HttpServlet servlet = getServletInstance(anctionPackage + className);
		if (servlet == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		if ((servlet instanceof Controller)) {
			Controller controller = (Controller)servlet;
			controller.setPathList(pathList);
		}
		
		servlet.service(request, response);
	}

	private int splitPath(String path, String[] list) {
		int i0 = 1;
		int count = 0;
		
		int p1 = path.indexOf('?');
		if (p1 > 0) {
			path = path.substring(0, p1);
		}
		int p2 = path.lastIndexOf('.');
		if (p2 > 0) {
			list[3] = path.substring(p2, path.length()); 
			path = path.substring(0, p2);
		}
		
		if (path.length() <= 2) {
			return 0;
		}
		
		if (path.charAt(0) != '/') {
			i0 = 0;
		}
		while (count < 3) {
			int i1 = path.indexOf('/', i0);
			if (i1 >= 0) {
				list[count] = path.substring(i0, i1);
				++count;
				if (i1 == path.length() -1) {
					return count;
				}
				i0 = i1+1;
			} else {
				list[count] = path.substring(i0);
				++count;
				return count;
			}
		}
		return count;
	}
	
	private HttpServlet getServletInstance(String className)	{
		Class<?> type;
		try {
			type = Class.forName(className);
			return (HttpServlet) type.newInstance();
		} catch (ClassNotFoundException e) {
			return null;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (Throwable e) {
			return null;
		}
	}
}
