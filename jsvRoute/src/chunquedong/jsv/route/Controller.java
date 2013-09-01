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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public abstract class Controller {
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	private String[] pathList;
	private String methodName;
	private String extName;
	
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (!before()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		try {
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
			
		  invoke(method, param);
		  
		} catch (Throwable e) {
			onError(e);
		} finally {
			after();
		}
	}
	
	private void invoke(Method method, String param) throws IOException {
		int paramSize = method.getParameterTypes().length;
		try {
			if (paramSize == 1) {
				if (method.getParameterTypes()[0] == long.class
						|| method.getParameterTypes()[0] == Long.class) {
					long l = Long.parseLong(param);
					method.invoke(this, l);
				} else if (method.getParameterTypes()[0] == int.class
						|| method.getParameterTypes()[0] == Integer.class) {
					int l = Integer.parseInt(param);
					method.invoke(this, l);
				} else {
					method.invoke(this, param);
				}
			} else if (paramSize == 0) {
				method.invoke(this);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		} catch (IllegalArgumentException e) {
			onError(e);
		} catch (IllegalAccessException e) {
			onError(e);
		} catch (InvocationTargetException e) {
			onError(e.getCause());
		} catch (Throwable e) {
			onError(e);
		}
	}
	
	String[] getPathList() {
		return pathList;
	}
	void setPathList(String[] pathList) {
		this.pathList = pathList;
	}
	
	protected String getExtName() {
		return extName;
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
	
	protected boolean before() {
		return true;
	}
	
	protected void after() {
	}
	
	protected void onError(Throwable e) {
		try {
			if (RouteServlet.isDebug()) {
				e.printStackTrace(response.getWriter());
			} else {
		      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (IOException e1) {
      e1.printStackTrace();
    }
	}
	
	protected void render(String templateFile) {
		setContentType();
		VelocityRender.getInstance().doRender(templateFile, request, response);
	}
	
	protected void setContentType() {
		if (extName.equals(".vm")) {
			setContentType("text/html; charset=utf-8");
		} else {
			setContentType("text/"+extName+"; charset=utf-8");
		}
	}
	
	protected void setContentType(String type) {
		response.setHeader("Content-Type", type);
	}
	
	protected void render() {
		render("./view/" + this.getClass().getSimpleName() + "/" + methodName + extName);
	}
}
