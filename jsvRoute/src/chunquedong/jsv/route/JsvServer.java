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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
 
public class JsvServer extends AbstractHandler {
	
	private HttpServlet rootServlet;
	
	public HttpServlet getRootServlet() {
		return rootServlet;
	}

	public void setRootServlet(HttpServlet rootServlet) {
		this.rootServlet = rootServlet;
	}
 
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (!target.startsWith("/public/")) {
			rootServlet.service(request, response);
			baseRequest.setHandled(true);
		}
	}
	
	public static void start(String packageName, int port, String classPath
			, boolean isDebug, String profix) throws Exception {
		Server server = new Server(port);
		
		ResourceHandler resourceandler = new ResourceHandler();
		resourceandler.setDirectoriesListed(true);
		resourceandler.setWelcomeFiles(new String[] { "index.html" });
		resourceandler.setResourceBase(".");
		
		//ServletHandler servletHandler = new ServletHandler();
		//servletHandler.addServletWithMapping(HelloServlet.class, "/*");
		JsvServer servletHandler = new JsvServer();
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
		String packageName = "chunquedong.jsv.action.";
		int port = 8080;
		String classPath = "./bin";
		boolean isDebug = true;
		if (args.length == 3) {
			packageName = args[0];
			port = Integer.parseInt(args[1]);
			classPath = args[3];
			isDebug = Boolean.parseBoolean(args[4]);
		} else if (args.length == 1) {
			System.out.println("1.packageName; 2.port; 3.classPath; 4.isDebug");
			return;
		}
		start(packageName, port, classPath, isDebug, null);
	}
}