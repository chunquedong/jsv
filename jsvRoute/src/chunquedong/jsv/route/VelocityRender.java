//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.route;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class VelocityRender {
	private static VelocityRender instance = new VelocityRender();
	public static VelocityRender getInstance() { return instance; }
	
	private VelocityRender() {
		Velocity.init();
	}
	
	public void doRender(String templateFile, ServletRequest request, ServletResponse response) {
		try {
			doRender(templateFile, request, response.getWriter(), response.getCharacterEncoding());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void doRender(String templateFile, ServletRequest request, Writer writer, String encoding)
	{
		VelocityContext context = new VelocityContext();
		for (Enumeration<String> attrs=request.getAttributeNames(); attrs.hasMoreElements();) {
			String attrName = attrs.nextElement();
			context.put(attrName, request.getAttribute(attrName));
		}
				
		try
		{
			Template template = Velocity.getTemplate(templateFile, encoding);
			template.merge(context, writer);
		}
		catch( ResourceNotFoundException rnfe )
		{
				System.out.println("Example : error : cannot find template " + templateFile );
		}
		catch( ParseErrorException pee )
		{
				System.out.println("Example : Syntax error in template " + templateFile + ":" + pee );
		}
	}
}
