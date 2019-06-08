//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import chunquedong.jsv.record.connect.DbUtil;
import chunquedong.jsv.record.model.*;
import chunquedong.jsv.record.sql.SqlExecutor;
import chunquedong.jsv.record.sql.dialect.SqlDialect;

public class Context {
	private Connection connection;
	private SqlExecutor executor = new SqlExecutor();
	
	public Connection getConnection() {
		return connection;
	}
	
	public void setConnection(Connection conn) {
		connection = (conn);
	}
	
	public SqlDialect getDialect() {
		return executor.dialect;
	}

	public void setDialect(SqlDialect dialect) {
		this.executor.dialect = dialect;
	}
	
	//////////////////////////////////////////////////////////////////////////
	//Execute write
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 * insert this obj to database
	 */
	public void insert(Record obj)
	{
		this.executor.insert(obj.getSchema(), this.getConnection(), obj);
	}
	
	/**
	 *	update by id
	 */
	public void update(Record obj)
	{
		this.executor.updateById(obj.getSchema(), this.getConnection(), obj);
	}
	
	/**
	 *	delete by example
	 */
	public void deleteByExample(Record obj)
	{
		this.executor.delete(obj.getSchema(), this.getConnection(), obj);
	}
	
	/**
	 *	delete by id
	 */
	public void deleteById(Schema table, Object id)
	{
		this.executor.removeById(table, this.getConnection(), id);
	}
	
	//////////////////////////////////////////////////////////////////////////
	//select
	//////////////////////////////////////////////////////////////////////////
	
	public List<Record> list(Record obj) {
		return (List<Record>)list(obj, "", 0, 50);
	}
	/** 
	 * select by example
	 * @param obj
	 * @param orderby
	 * @return
	 */
	public List<Record> list(Record obj, String orderby, int offset, int limit)
	{
		return executor.select(obj.getSchema(), this.getConnection(), obj, orderby, offset, limit);
	}
	
	public Record one(Record obj) {
		return one(obj, "");
	}
	/**
	 *	select by example and get the first one
	 */
	public Record one(Record obj, String orderby)
	{
		return (Record)executor.selectOne(obj.getSchema(), this.getConnection(), obj, orderby);
	}
	
	//////////////////////////////////////////////////////////////////////////
	//Select where
	//////////////////////////////////////////////////////////////////////////
	
	public List<Record> select(Schema table, String condition, Object[] params) {
		return select(table, condition, params, 0, 50);
	}
	
	/**
	 *	query by condition
	 * @param table
	 * @param condition
	 * @param offset
	 * @return
	 */
	public List<Record> select(Schema table, String condition, Object[] params
			, int offset, int limit)
	{
		return this.executor.selectWhere(table, this.getConnection(), condition
				, params, offset, limit);
	}
	
	public List<Record> query(String sql, Object[] params) {
		try {
			java.sql.PreparedStatement stmt = this.getConnection().prepareStatement(sql);
			if (params != null) {
				for (int i=0; i<params.length; ++i) {
					stmt.setObject(i+1, params[i]);
				}
			}
			
			if (SqlExecutor.log.isLoggable(Level.FINE))
			{
				SqlExecutor.log.fine(sql);
				SqlExecutor.log.fine(Arrays.toString(params));
			}
			
			List<Record> list = new ArrayList<Record>();
			Schema schema = null;
			ResultSet rs= stmt.executeQuery();
			while (rs.next()) {
				java.sql.ResultSetMetaData meta = rs.getMetaData();
				if (schema == null) {
					schema = new Schema("temp");
					for (int i=0; i<meta.getColumnCount(); i++) {
						String name = meta.getColumnLabel(i+1);
						String type = meta.getColumnTypeName(i+1);
//						String clz = meta.getColumnClassName(i+1);
						Field f = new Field(name.toLowerCase(), type);
						schema.add(f);
					}
				}
				Record r = schema.newInstance();
				for (int i=0; i<meta.getColumnCount(); i++) {
					if (meta.getColumnType(i+1) == java.sql.Types.CLOB) {
						r.set(i, rs.getString(i+1));
					}
					else {
						r.set(i, rs.getObject(i+1));
					}
				}
				list.add(r);
			}
			rs.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//////////////////////////////////////////////////////////////////////////
	//By ID
	//////////////////////////////////////////////////////////////////////////
	
	public boolean loadById(Schema table, Record obj)
	{
		Object id = table.getPk().getValue(obj);
		return this.executor.loadById(table, this.getConnection(), id, obj);
	}

	
	//////////////////////////////////////////////////////////////////////////
	//Extend method
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 *	count by example
	 */
	public long count(Record obj)
	{
		return this.executor.count(obj.getSchema(), this.getConnection(), obj);
	}
	
	/**
	 *	exist by example, this operate noCache
	 * @param obj
	 * @return
	 */
	public boolean exist(Record obj)
	{
		long n = this.executor.count(obj.getSchema(), this.getConnection(), obj);
		return n > 0;
	}
	
	/**
	 *	update or insert
	 * @param obj
	 * @return
	 */
	public void save(Record obj)
	{
		if (existById(obj))
		{
			update(obj);
		}
		else
		{
			insert(obj);
		}
	}
	
	public boolean existById(Record obj)
	{
		Object id = obj.getId();
		if (this.executor.existById(obj.getSchema(), this.getConnection(), id))
		{
			return true;
		}
		return false;
	}
	//////////////////////////////////////////////////////////////////////////
	//Table operate
	//////////////////////////////////////////////////////////////////////////
	
	public void createTable(Schema table)
	{
		this.executor.createTable(table, this.getConnection());
	}
	
	public void dropTable(Schema table)
	{
		this.executor.dropTable(table, this.getConnection());
	}
	
	public boolean tableExists(Schema table)
	{
		return DbUtil.hasTable(this.getConnection(), table.getName());
	}
	
	/**
	 *	check the object table is fit to database table
	 * @param table
	 * @return
	 */
	public boolean checkTable(Schema table)
	{
		DatabaseMetaData meta = null;
		ResultSet trow = null;
		try{
			meta = this.getConnection().getMetaData();
			trow = meta.getTables(null, null, table.getName(), null);
			if (trow.next()) {
				this.getConnection();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseResultSet(trow);
		}
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////
	//Transaction
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 *	transaction , if error will auto roolback
	 */
	public void trans(Function f)
	{
		Connection conn = this.getConnection();
		boolean oauto = true;
		try
		{
			oauto = conn.getAutoCommit();
			conn.setAutoCommit(false);
			f.call(this);
			conn.commit();
		}
		catch (Exception e)
		{
			try {
				conn.rollback();
			} catch (SQLException e1) {
				throw new RuntimeException(e1);
			}
			throw new RuntimeException(e);
		}
		finally
		{
			try {
				conn.setAutoCommit(oauto);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			DbUtil.closeConnection(conn);
		}
	}
}
