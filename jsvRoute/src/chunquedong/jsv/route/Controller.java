//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.route;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public abstract class Controller	extends HttpServlet {
	private static final long serialVersionUID = 1784867224406169093L;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	private String[] pathList;
	private String methodName;
	private String extName;
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		methodName = pathList[1];
		String param = pathList[2];
		if (methodName == null) {
			methodName = "index";
		}
		extName = pathList[3];
		if (extName == null) {
			extName = ".vm";
		}

		Method method = getMethod(this, methodName);
		if (method == null) {
			if (param == null && !methodName.equals("index")) {
				param = methodName;
				methodName = request.getMethod().toLowerCase();
				method = getMethod(this, methodName);
				if (method == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}
		if (!requesetMethodCheck(method, request.getMethod())) {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}
		
		this.request = request;
		this.response = response;
		
		int paramSize = method.getParameterAnnotations().length;
		try {
			if (paramSize == 1) {
				method.invoke(this, param);
			} else if (paramSize == 0) {
				method.invoke(this);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	String[] getPathList() {
		return pathList;
	}
	void setPathList(String[] pathList) {
		this.pathList = pathList;
	}
	
	private boolean requesetMethodCheck(Method method, String name) {
		if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
			return false;
		}
		if (name.equals("GET")) {
			if (method.isAnnotationPresent(Action.Post.class)
					|| method.isAnnotationPresent(Action.Put.class)
					|| method.isAnnotationPresent(Action.Delete.class)) {
				return false;
			}
			return true;
		}
		else if (name.equals("POST")) {
			return method.isAnnotationPresent(Action.Post.class);
		}
		else if (name.equals("PUT")) {
			return method.isAnnotationPresent(Action.Put.class);
		}
		else if (name.equals("DELETE")) {
			return method.isAnnotationPresent(Action.Delete.class);
		}
		
		return false;
	}
	
	private Method getMethod(Object obj, String method) {
		Method[] methods = obj.getClass().getDeclaredMethods();
		for (int i=0; i<methods.length; ++i) {
			if (methods[i].getName().equals(method)) {
				return methods[i];
			}
		}
		return null;
	}
	
	public void render(String templateFile) {
		VelocityRender.getInstance().doRender(templateFile, request, response);
	}
	
	public void render() {
		render("./view/" + this.getClass().getSimpleName() + "/" + methodName + extName);
	}
}
