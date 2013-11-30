//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.connect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * @author yangjiandong
 *
 */
public class DbUtil {
	public static boolean hasTable(Connection con, String table){
		try {
			DatabaseMetaData meta = con.getMetaData();
			if (checkHasTable(table, meta))
				return true;
			if (checkHasTable(table.toLowerCase(), meta))
				return true;
			if (checkHasTable(table.toUpperCase(), meta))
				return true;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	private static boolean checkHasTable(String table, DatabaseMetaData meta)
			throws SQLException {
		boolean has = false;
		ResultSet rs = meta.getTables(null, null, table, null);
		if (rs.next()) {
			has = true;
		}
		colseResultSet(rs);
		return has;
	}
	
	public static Connection getConnection(String driver, String url
			, String userName, String passWord) {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		Connection connection;
		try {
			connection = DriverManager.getConnection(url, userName,
					passWord);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return connection;
	}
	
	public static void colseStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void colseResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

}