package chunquedong.jsv.demo;

import chunquedong.jsv.route.JsvServer;

public class Main {
	public static void main(String[] args) throws Exception {
		JsvServer server = new JsvServer();
		server.setPackageName("chunquedong.jsv.demo.action.");
		server.setPort(8080);
		server.setDebug(true);
		server.start();
	}
}
