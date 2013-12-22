//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.route;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class VelocityRender {
	private static VelocityRender instance = new VelocityRender();

	public static VelocityRender getInstance() {
		return instance;
	}

	public void init(String fileDir) {
		if (fileDir == null) {
			java.net.URL url = Controller.class.getResource(File.separator);
			String classPath = url.getPath();
			int i = classPath.lastIndexOf(File.separatorChar);
			String subStr = classPath.substring(0, i);
			i = subStr.lastIndexOf(File.separatorChar);
			fileDir = classPath.substring(0, i + 1);
			System.out.println("Velocity fileDir:" + fileDir);
		}
		Properties properties = new Properties();
		properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, fileDir);
		Velocity.init(properties);
	}

	public void doRender(String templateFile, ServletRequest request,
	    ServletResponse response) {
		try {
			doRender(templateFile, request, response.getWriter(),
			    response.getCharacterEncoding());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doRender(String templateFile, ServletRequest request,
	    PrintWriter writer, String encoding) {
		VelocityContext context = new VelocityContext();
		for (Enumeration<String> attrs = request.getAttributeNames(); attrs
		    .hasMoreElements();) {
			String attrName = attrs.nextElement();
			context.put(attrName, request.getAttribute(attrName));
		}

		try {
			Template template = Velocity.getTemplate(templateFile, encoding);
			template.merge(context, writer);
		} catch (ResourceNotFoundException rnfe) {
			if (RouteServlet.isDebug()) {
				rnfe.printStackTrace(writer);
			}
		} catch (ParseErrorException pee) {
			if (RouteServlet.isDebug()) {
				pee.printStackTrace(writer);
			}
		}
	}
}
