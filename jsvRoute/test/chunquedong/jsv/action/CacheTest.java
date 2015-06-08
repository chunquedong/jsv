package chunquedong.jsv.action;

import chunquedong.jsv.route.Action;
import chunquedong.jsv.route.CachedController;

public class CacheTest extends CachedController {
	static int count = 0;
	
	@Action.Cache(expiry = 100)
	public void getCache(String name) {
		this.sendText(name+":"+count);
	}

	public void index() {
		this.sendText(""+count);
	}
	
	public void add() {
		this.sendText(""+count);
		++count;
	}
}
