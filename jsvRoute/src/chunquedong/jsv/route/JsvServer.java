//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.route;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
 
public class JsvServer {
	private String packageName = "chunquedong.jsv.action.";
	private int port = 8080;
	private String classPath = "./bin";
	private boolean isDebug = true;
	private String profix = null;
	private String resourceBase = ".";
	
	
	public void start() throws Exception {
		Server server = new Server(port);
		
		ResourceHandler resourceandler = new ResourceHandler();
		resourceandler.setDirectoriesListed(true);
		resourceandler.setWelcomeFiles(new String[] { "index.html" });
		resourceandler.setResourceBase(resourceBase);
		
		//ServletHandler servletHandler = new ServletHandler();
		//servletHandler.addServletWithMapping(HelloServlet.class, "/*");
		ServletHandler servletHandler = new ServletHandler();
		RouteServlet routeServlet = new RouteServlet();
		
		routeServlet.setActionPackage(packageName);
		routeServlet.setClassPath(classPath);
		routeServlet.setProfix(profix);
		RouteServlet.setDebugMode(isDebug);
		servletHandler.setRootServlet(routeServlet);
		
		SessionManager sm = new HashSessionManager();
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { new SessionHandler(sm), servletHandler, resourceandler, new DefaultHandler() });
		
		server.setHandler(handlers);
		server.start();
		server.join();
	}
 
	public static void main(String[] args) throws Exception {
		JsvServer server = new JsvServer();
		if (args.length == 3) {
			server.packageName = args[0];
			server.port = Integer.parseInt(args[1]);
			server.classPath = args[3];
			server.isDebug = Boolean.parseBoolean(args[4]);
		} else if (args.length == 1) {
			System.out.println("1.packageName; 2.port; 3.classPath; 4.isDebug");
			return;
		}
		server.start();
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public boolean isDebug() {
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	public String getProfix() {
		return profix;
	}

	public void setProfix(String profix) {
		this.profix = profix;
	}

	public String getResourceBase() {
		return resourceBase;
	}

	public void setResourceBase(String resourceBase) {
		this.resourceBase = resourceBase;
	}
}