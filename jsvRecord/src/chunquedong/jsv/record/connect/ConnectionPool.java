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
import java.util.NoSuchElementException;
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
	
	public synchronized Connection open() {
		while (list.size() > 0) {
			try {
				Connection conn = list.poll();
				boolean valid = true;
				try {
					valid = conn.isValid(1);
				} catch (Throwable ex) {
					System.err.println("ERROR:"+ex.getMessage());
				}
				
				try {
					if (valid && conn != null && !conn.isClosed()) {
						return conn;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					DbUtil.closeConnection(conn);
				}
			} catch (NoSuchElementException e) {
			}
		}
		return DbUtil.getConnection(driver, url, userName, passWord);
	}
	
	public synchronized void close(Connection conn) {
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
			DbUtil.closeConnection(conn);
		}
	}
	
	public synchronized void closeAll() {
		for (Connection conn : list) {
			DbUtil.closeConnection(conn);
		}
		list.clear();
	}
}
