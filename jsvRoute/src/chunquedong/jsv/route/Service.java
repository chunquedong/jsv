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
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
 
public class Service extends AbstractHandler {
	
	private HttpServlet rootServlet;
	
	public HttpServlet getRootServlet() {
		return rootServlet;
	}

	public void setRootServlet(HttpServlet rootServlet) {
		this.rootServlet = rootServlet;
	}
 
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (!target.startsWith("/public")) {
			rootServlet.service(request, response);
			baseRequest.setHandled(true);
		}
	}
 
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		
		ResourceHandler resourceandler = new ResourceHandler();
		resourceandler.setDirectoriesListed(true);
		resourceandler.setWelcomeFiles(new String[] { "index.html" });
		resourceandler.setResourceBase(".");
		
		//ServletHandler servletHandler = new ServletHandler();
		//servletHandler.addServletWithMapping(HelloServlet.class, "/*");
		Service servletHandler = new Service();
		RouteServlet routeServlet = new RouteServlet();
		routeServlet.setAnctionPackage("chunquedong.jsv.action.");
		servletHandler.setRootServlet(routeServlet);
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { servletHandler, resourceandler, new DefaultHandler() });
		
		server.setHandler(handlers);
		server.start();
		server.join();
	}
}