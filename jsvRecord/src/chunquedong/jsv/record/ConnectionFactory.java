package chunquedong.jsv.record;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import chunquedong.jsv.record.connect.ConnectionPool;
import chunquedong.jsv.record.model.Schema;

public class ConnectionFactory {
	static Logger log = Logger.getLogger("jsvRecord");
	private ConnectionPool connectionPool;
	private Context context;
	private static ConnectionFactory instance = new ConnectionFactory();

	static {
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		log.addHandler(handler);
		log.setLevel(Level.ALL);
	}

	public static ConnectionFactory getInstance() {
		return instance;
	}

	public Context getContext() {
		return context;
	}

	public void init(String driver, String url, String userName, String passWord,
	    int poolSize, String level) {
		Level l = Level.parse(level);
		log.setLevel(l);
		connectionPool = new ConnectionPool(driver, url, userName, passWord,
		    poolSize);
		context = new Context();
	}

	public void initFromConfig(String path) {
		if (path == null) {
			String driver = "org.h2.Driver";
			String url = "jdbc:h2:./test";
			String userName = "postgres";
			String passWord = "111";
			
			ConnectionFactory.getInstance().init(driver, url, userName, passWord, 20, "ALL");
			return;
		}
		
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(path));

			Properties p = new Properties();
			p.load(in);

			String driver = p.getProperty("driver", "org.h2.Driver");
			String url = p.getProperty("url", "jdbc:h2:./test");
			String userName = p.getProperty("userName", "postgres");
			String passWord = p.getProperty("passWord", "111");
			String level = p.getProperty("level", "ALL");
			String poolSize = p.getProperty("poolSize", "20");
			init(driver, url, userName, passWord, Integer.parseInt(poolSize), level);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void closeAll() {
		connectionPool.closeAll();
	}

	public boolean open() {
		try {
			Context.setConnection(connectionPool.open());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void close() {
		connectionPool.close(context.getConnection());
		Context.setConnection(null);
	}

	public void createTable(Schema table, boolean drop) {
		if (getContext().tableExists(table)) {
			if (drop) {
				getContext().dropTable(table);
			} else {
				return;
			}
		}
		getContext().createTable(table);
	}
}
