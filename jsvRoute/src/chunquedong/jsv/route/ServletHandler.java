package chunquedong.jsv.route;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class ServletHandler extends AbstractHandler {
	static Logger log = Logger.getLogger("jsvRoute");

	private HttpServlet rootServlet;

	public HttpServlet getRootServlet() {
		return rootServlet;
	}

	public void setRootServlet(HttpServlet rootServlet) {
		this.rootServlet = rootServlet;
	}
	
	public static String getUrl(HttpServletRequest req) {
	    String reqUrl = req.getRequestURL().toString();
	    String queryString = req.getQueryString();
	    if (queryString != null) {
	        reqUrl += "?"+queryString;
	    }
	    return reqUrl;
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (RouteServlet.isDebug()) {
			log.info(getUrl(request));
		}

		if (target.endsWith("favicon.ico")) {
			return;
		}

		if (!target.startsWith("/public/")) {
			rootServlet.service(request, response);
			baseRequest.setHandled(true);
		}
	}
}
