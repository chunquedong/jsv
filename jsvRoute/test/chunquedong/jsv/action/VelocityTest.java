package chunquedong.jsv.action;
import java.util.ArrayList;

import chunquedong.jsv.route.Controller;

public class VelocityTest extends Controller {
	private static final long serialVersionUID = 1L;
	
	public void index() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("ArrayList element 1");
		list.add("ArrayList element 2");
		list.add("ArrayList element 3");
		list.add("ArrayList element 4<hr/>");
		request.setAttribute("list", list);
		
		render();
	}
}
