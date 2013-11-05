//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.route;
import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class RouteServlet extends HttpServlet {
	
	private static final long serialVersionUID = -7749777676921475430L;
	private String actionPackage = "";
	private static boolean isDebug = true;
	private static HotReloader reloader = null;
	
	@Override
  public void init() throws ServletException {
		String debug = super.getInitParameter("debugMode");
		isDebug = ("debug".equals(debug));
		actionPackage = super.getInitParameter(actionPackage);
		if (actionPackage == null) {
			actionPackage = "";
		}
  }
	
	public static boolean isDebug() {
		return isDebug;
	}
	
	public static void setDebugMode(boolean debug) {
		isDebug = debug;
	}
	
	public void setActionPackage(String anctionPackage) {
		if (!anctionPackage.endsWith(".")) {
			anctionPackage += ".";
		}
		this.actionPackage = anctionPackage;
  }
	
	public void setClassPath(String classPath) {
		reloader = new HotReloader(classPath);
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
		
		Object servlet = getServletInstance(actionPackage + className);
		if (servlet == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		if ((servlet instanceof Controller)) {
			Controller controller = (Controller)servlet;
			controller.setPathList(pathList);
			controller.service(request, response);
		} else if (servlet instanceof Servlet) {
			Servlet s = (Servlet)servlet;
			s.service(request, response);
		} else {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			return;
		}
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
	
	private Object getServletInstance(String className)	{
		Class<?> type;
		try {
			if (isDebug()) {
				type = reloader.findClass(className);
			} else {
				type = Class.forName(className);
			}
			return type.newInstance();
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
