package chunquedong.jsv.demo.action;

import java.io.IOException;

import chunquedong.jsv.route.Controller;

public class Test extends Controller {
	public void get(long id) throws IOException {
		response.getWriter().println("get" + id);
	}
}