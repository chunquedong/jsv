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
import java.util.List;

import chunquedong.jsv.record.connect.DbUtil;
import chunquedong.jsv.record.model.*;
import chunquedong.jsv.record.sql.SqlExecutor;

public class Context {
	private static ThreadLocal<Connection> connection = new ThreadLocal<Connection>();
	private SqlExecutor executor = new SqlExecutor();
	
	public Connection getConnection() {
		if (connection.get() != null) {
			return connection.get();
		}
		throw new RuntimeException("no found connection");
	}
	
	public static void setConnection(Connection conn) {
		connection.set(conn);
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
		this.executor.update(obj.getSchema(), this.getConnection(), obj);
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
	
	public List<Record> select(Schema table, String condition) {
		return select(table, condition, 0, 50);
	}
	/**
	 *	query by condition
	 * @param table
	 * @param condition
	 * @param offset
	 * @return
	 */
	public List<Record> select(Schema table, String condition, int offset, int limit)
	{
		return this.executor.selectWhere(table, this.getConnection(), condition, offset, limit);
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
	
	private boolean existById(Record obj)
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
			DbUtil.colseConnection(conn);
		}
	}
}
