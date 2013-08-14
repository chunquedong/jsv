//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.connect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ConnectionPool {
	private Queue<Connection> list;
	
	private String driver;
	private String url;
	private String userName;
	private String passWord;
	
	public ConnectionPool(String driver, String url
			, String userName, String passWord, int capacity) {
		this.driver = driver;
		this.url = url;
		this.userName = userName;
		this.passWord = passWord;
		
		list = new ArrayBlockingQueue<Connection>(capacity);
	}
	
	public Connection open() {
		while (list.size() > 0) {
			Connection conn = list.poll();
			try {
				if (!conn.isClosed() && conn.isValid(10)) {
					return conn;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DbUtil.colseConnection(conn);
			}
		}
		return DbUtil.getConnection(driver, url, userName, passWord);
	}
	
	public void close(Connection conn) {
		try {
			if (conn.isClosed()) {
				return;
			}
			if (conn.getAutoCommit() == false) {
				conn.rollback();
				conn.setAutoCommit(true);
			}
		} catch (SQLException e) {
			return;
		}
		
		boolean r = list.offer(conn);
		if (!r) {
			DbUtil.colseConnection(conn);
		}
	}
	
	public synchronized void clearAll() {
		for (Connection conn : list) {
			DbUtil.colseConnection(conn);
		}
		list.clear();
	}
}
