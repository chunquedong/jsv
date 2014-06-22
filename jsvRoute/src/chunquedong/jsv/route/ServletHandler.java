package chunquedong.jsv.route;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class ServletHandler extends AbstractHandler {
			
			private HttpServlet rootServlet;
			
			public HttpServlet getRootServlet() {
				return rootServlet;
			}

			public void setRootServlet(HttpServlet rootServlet) {
				this.rootServlet = rootServlet;
			}
		 
			@Override
			public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
				if (target.endsWith("favicon")) {
					return;
				}
				
				if (!target.startsWith("/public/")) {
					rootServlet.service(request, response);
					baseRequest.setHandled(true);
				}
			}
}
