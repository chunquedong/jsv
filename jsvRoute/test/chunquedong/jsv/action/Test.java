package chunquedong.jsv.action;
import java.io.IOException;

import chunquedong.jsv.route.Action;
import chunquedong.jsv.route.Controller;



public class Test extends Controller {
	private static final long serialVersionUID = 1L;

	public void get(String id) throws IOException {
		response.getWriter().println("get:"+id);
	}
	
	public void index() throws IOException {
		response.getWriter().println("index");
	}
	
	@Action.Post
	public void post(String id) throws IOException {
		response.getWriter().println("post:"+id);
	}
	
	public void foo1() throws IOException {
		response.getWriter().println("foo1:");
	}
	
	@Action.Post
	public void foo2() throws IOException {
		response.getWriter().println("foo2:");
	}
	
	void foo3() throws IOException {
		response.getWriter().println("foo1:");
	}
}
