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
	protected boolean autoSetContentType = true;

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
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
			
			if (autoSetContentType) {
				setContentType();
			}
			invoke(method, param);

		} catch (Throwable e) {
			onError(e);
		} finally {
			after();
		}
	}

	protected void invoke(Method method, String param) throws IOException {
		int paramSize = method.getParameterTypes().length;
		try {
			Object ret;
			if (paramSize == 1) {
				if (method.getParameterTypes()[0] == long.class
						|| method.getParameterTypes()[0] == Long.class) {
					long l = Long.parseLong(param);
					ret = method.invoke(this, l);
				} else if (method.getParameterTypes()[0] == int.class
						|| method.getParameterTypes()[0] == Integer.class) {
					int l = Integer.parseInt(param);
					ret = method.invoke(this, l);
				} else {
					ret = method.invoke(this, param);
				}
			} else if (paramSize == 0) {
				ret = method.invoke(this);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			// send result
			if (ret != null) {
				response.getWriter().print(ret);
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
		
		if (method.isAnnotationPresent(Action.Forbid.class)) {
			return false;
		}
		
		if (name.equals("GET")) {
			if (method.isAnnotationPresent(Action.Post.class)
					|| method.isAnnotationPresent(Action.Put.class)
					|| method.isAnnotationPresent(Action.Delete.class)) {
				return false;
			}
			return true;
		} else if (name.equals("POST")) {
			return method.isAnnotationPresent(Action.Post.class);
		} else if (name.equals("PUT")) {
			return method.isAnnotationPresent(Action.Put.class);
		} else if (name.equals("DELETE")) {
			return method.isAnnotationPresent(Action.Delete.class);
		}

		return false;
	}

	private Method getMethod(Object obj, String method) {
		Method[] methods = obj.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; ++i) {
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
	
	public static String getUrl(HttpServletRequest req) {
	    String reqUrl = req.getRequestURL().toString();
	    String queryString = req.getQueryString();
	    if (queryString != null) {
	        reqUrl += "?"+queryString;
	    }
	    return reqUrl;
	}

	protected void onError(Throwable e) {
		System.err.println("request error:" + getUrl(request) + ", cause:"+ e.getMessage());
		try {
			if (RouteServlet.isDebug()) {
				e.printStackTrace();
				e.printStackTrace(response.getWriter());
				sendError();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (IOException e1) {
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
			setContentType("text/" + extName + "; charset=utf-8");
		}
	}

	protected void setContentType(String type) {
		response.setHeader("Content-Type", type);
	}

	protected void render() {
		render("view/" + this.getClass().getSimpleName() + "/" + methodName
				+ extName);
	}

	// ////////////////////////////////////////////////////////////////////

	protected void sendText(String msg) {
		try {
			response.getWriter().print(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void sendOk() {
		try {
			response.sendError(HttpServletResponse.SC_OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void sendError() {
		try {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void sendNotFound() {
		try {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void redirect(String path) {
		try {
			response.sendRedirect(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String getString(String name, String defaultValue) {
		String s = request.getParameter(name);
		if (s == null)
			return defaultValue;
		return s;
	}

	protected boolean getBoolean(String name, boolean defaultValue) {
		String s = request.getParameter(name);
		if (s == null)
			return defaultValue;
		return "true".equals(s);
	}

	protected boolean isPresent(String name) {
		return request.getParameter(name) != null;
	}

	protected int getInt(String name, int defaultValue) {
		String s = request.getParameter(name);
		if (s == null)
			return defaultValue;
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	protected long getLong(String name, long defaultValue) {
		String s = request.getParameter(name);
		if (s == null)
			return defaultValue;
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	protected float getFloat(String name, float defaultValue) {
		String s = request.getParameter(name);
		if (s == null)
			return defaultValue;
		try {
			return Float.parseFloat(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	protected double getDouble(String name, double defaultValue) {
		String s = request.getParameter(name);
		if (s == null)
			return defaultValue;
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
