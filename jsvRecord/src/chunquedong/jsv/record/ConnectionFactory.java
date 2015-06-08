package chunquedong.jsv.record;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import chunquedong.jsv.record.connect.ConnectionPool;
import chunquedong.jsv.record.model.Schema;
import chunquedong.jsv.record.sql.dialect.H2Dialect;
import chunquedong.jsv.record.sql.dialect.SqlDialect;

public class ConnectionFactory {
	static Logger log = Logger.getLogger("jsvRecord");
	private ConnectionPool connectionPool;
	private SqlDialect dialet = null;
	private ThreadLocal<Context> context = new ThreadLocal<Context>();

	static {
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		log.addHandler(handler);
		log.setLevel(Level.ALL);
	}

	public void init(String driver, String url, String userName, String passWord,
	    int poolSize, String level) {
		Level l = Level.parse(level);
		log.setLevel(l);
		connectionPool = new ConnectionPool(driver, url, userName, passWord,
		    poolSize);
		
		if ("org.h2.Driver".equals(driver)) {
			dialet = (new H2Dialect());
		}
	}

	public void initFromConfig(String path) {
		if (path == null) {
			String driver = "org.h2.Driver";
			String url = "jdbc:h2:./test";
			String userName = "postgres";
			String passWord = "111";
			
			this.init(driver, url, userName, passWord, 20, "ALL");
			return;
		}
		
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(path));

			Properties p = new Properties();
			p.load(in);

			String driver = p.getProperty("driver", "org.sqlite.JDBC");
			String url = p.getProperty("url", "jdbc:sqlite:test.db");
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
		context = new ThreadLocal<Context>();
		connectionPool.closeAll();
	}

	public Context getContext() {
		Context cx = context.get();
		if (cx == null) {
			try {
				cx = new Context();
				cx.setConnection(connectionPool.open());
				if (dialet != null) {
					cx.setDialect(dialet);
				}
				context.set(cx);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return cx;
	}

	public boolean close() {
		Context cx = context.get();
		if (cx == null) {
			return false;
		}
		
		Connection conn = cx.getConnection();
		if (conn != null) {
			connectionPool.close(conn);
			cx.setConnection(null);
		}
		context.set(null);
		return true;
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
