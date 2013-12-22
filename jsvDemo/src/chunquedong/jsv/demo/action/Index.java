package chunquedong.jsv.demo.action;

import java.io.IOException;

import chunquedong.jsv.route.Controller;

public class Index extends Controller {
	public void index() throws IOException {
		request.setAttribute("name", "JSV");
		render();
	}
}

