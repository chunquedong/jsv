//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.record.sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import chunquedong.jsv.record.connect.DbUtil;
import chunquedong.jsv.record.model.*;
import chunquedong.jsv.record.sql.dialect.SqlDialect;

public class SqlExecutor {
	static Logger log = Logger.getLogger("jsvRecord");
	public SqlDialect dialect = new SqlDialect();

	public void insert(Schema table, Connection db, Object obj)
	{
		String sql = InsertMaker.getSql(table);
		Object[] params = InsertMaker.getParam(table, obj);
		if (log.isLoggable(Level.FINE))
		{
			log.fine(sql);
			log.fine(Arrays.toString(params));
		}
		
		PreparedStatement stmt = null;
		ResultSet set = null;
		try {
			stmt = db.prepareStatement(sql);
			for (int i=0; i<params.length; ++i) {
				stmt.setObject(i+1, params[i]);
			}
			stmt.execute();
			if (table.isAutoGenerateId())
			{
				set = stmt.getGeneratedKeys();
				if (set.next())
				{
					table.getPk().setValue(obj, set.getObject(1));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseResultSet(set);
			DbUtil.colseStatement(stmt);
		}
	}
	
	public void updateById(Schema table, Connection db, Object obj)
	{
		StringBuilder sqlBuilder = new StringBuilder();
		UpdateMaker.getSql(sqlBuilder, table, obj);
		String sql = sqlBuilder.toString();
		Object[] params = UpdateMaker.getParam(table, obj);
		if (log.isLoggable(Level.FINE))
		{
			log.fine(sql);
			log.fine(Arrays.toString(params));
		}
		
		PreparedStatement stmt = null;
		try {
			stmt = db.prepareStatement(sql);
			for (int i=0; i<params.length; ++i) {
				stmt.setObject(i+1, params[i]);
			}
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseStatement(stmt);
		}
	}
	
	////////////////////////////////////////////////////////////////////////
	// select
	////////////////////////////////////////////////////////////////////////
	

	@SuppressWarnings("unchecked")
  public <T> List<T> select(Schema table, Connection db, Object obj
			, String orderby, int offset, int limit)
	{
		StringBuilder sqlBuilder = new StringBuilder();
		SelectMaker.getSql(sqlBuilder, table);
		WhereMaker.getSql(sqlBuilder, table, obj);
		if (!orderby.isEmpty()) sqlBuilder.append(" ").append(orderby);
		if (offset != 0 || limit != -1) {
			sqlBuilder.append(" limit ").append(offset).append(",").append(limit);
		}
		Object[] params = WhereMaker.getParam(table, obj);
		
		String sql = sqlBuilder.toString();
		if (log.isLoggable(Level.FINE))
		{
			log.fine(sql);
			log.fine(Arrays.toString(params));
		}
		
		PreparedStatement stmt = null;
		ResultSet set = null;
		List<T> list = new ArrayList<T>();
		try {
			stmt = db.prepareStatement(sql);
			for (int i=0; i<params.length; ++i) {
				stmt.setObject(i+1, params[i]);
			}
			//stmt.setMaxRows(offset+limit);
			set = stmt.executeQuery();
			//set.absolute(offset);
			while (set.next())
			{
				Object tobj = table.newInstance();
				SqlUtil.fillToObj(table, tobj, set);
				list.add((T)tobj);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseResultSet(set);
			DbUtil.colseStatement(stmt);
		}
		return list;
	}

	public Object selectOne(Schema table, Connection db, Object obj
			, String orderby)
	{
		StringBuilder sqlBuilder = new StringBuilder();
		SelectMaker.getSql(sqlBuilder, table);
		WhereMaker.getSql(sqlBuilder, table, obj);
		if (!orderby.isEmpty()) sqlBuilder.append(" ").append(orderby);
		Object[] params = WhereMaker.getParam(table, obj);
		
		String sql = sqlBuilder.toString();
		if (log.isLoggable(Level.FINE))
		{
			log.fine(sql);
			log.fine(Arrays.toString(params));
		}
		
		PreparedStatement stmt = null;
		ResultSet set = null;
		try {
			stmt = db.prepareStatement(sql);
			for (int i=0; i<params.length; ++i) {
				stmt.setObject(i+1, params[i]);
			}
			//stmt.setMaxRows(1);
			set = stmt.executeQuery();
			if (set.next())
			{
				Object tobj = table.newInstance();
				SqlUtil.fillToObj(table, tobj, set);
				return tobj;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseResultSet(set);
			DbUtil.colseStatement(stmt);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
  public <T> List<T> selectWhere(Schema table, Connection db, String condition
			, Object[] params, int offset, int limit)
	{
		StringBuilder sqlBuilder = new StringBuilder();
		SelectMaker.getSql(sqlBuilder, table);
		sqlBuilder.append(" from ").append(table.getName());
		if (!condition.isEmpty()) sqlBuilder.append(" ").append(condition);
		if (offset != 0 || limit != -1) {
			sqlBuilder.append(" limit ").append(offset).append(",").append(limit);
		}
		
		String sql = sqlBuilder.toString();
		if (log.isLoggable(Level.FINE))
		{
			log.fine(sql);
		}
		
		PreparedStatement stmt = null;
		ResultSet set = null;
		List<T> list = new ArrayList<T>();
		try {
			stmt = db.prepareStatement(sql);
			if (params != null) {
				for (int i=0; i<params.length; ++i) {
					stmt.setObject(i+1, params[i]);
				}
			}
			//stmt.setMaxRows(offset+limit);
			set = stmt.executeQuery();
			//set.absolute(offset);
			while (set.next())
			{
				Object obj = table.newInstance();
				SqlUtil.fillToObj(table, obj, set);
				list.add((T)obj);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseResultSet(set);
			DbUtil.colseStatement(stmt);
		}
		return list;
	}
	
	////////////////////////////////////////////////////////////////////////
	// other
	////////////////////////////////////////////////////////////////////////
	
	public boolean delete(Schema table, Connection db, Object obj)
	{
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("delete ");
		WhereMaker.getSql(sqlBuilder, table, obj);
		Object[] params = WhereMaker.getParam(table, obj);
		
		String sql = sqlBuilder.toString();
		if (log.isLoggable(Level.FINE))
		{
			log.fine(sql);
			log.fine(Arrays.toString(params));
		}
		
		PreparedStatement stmt = null;
		ResultSet set = null;
		try {
			stmt = db.prepareStatement(sql);
			for (int i=0; i<params.length; ++i) {
				stmt.setObject(i+1, params[i]);
			}
			boolean r = stmt.execute();
			return r;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseResultSet(set);
			DbUtil.colseStatement(stmt);
		}
	}
	
	public long count(Schema table, Connection db, Object obj)
	{
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select count(*)");
		WhereMaker.getSql(sqlBuilder, table, obj);
		Object[] params = WhereMaker.getParam(table, obj);
		
		String sql = sqlBuilder.toString();
		if (log.isLoggable(Level.FINE))
		{
			log.fine(sql);
			log.fine(Arrays.toString(params));
		}
		
		PreparedStatement stmt = null;
		ResultSet set = null;
		try {
			stmt = db.prepareStatement(sql);
			for (int i=0; i<params.length; ++i) {
				stmt.setObject(i+1, params[i]);
			}
			set = stmt.executeQuery();
			if (set.next())
			{
				long c = set.getLong(1);
				return c;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseResultSet(set);
			DbUtil.colseStatement(stmt);
		}
		return -1;
	}
	
	////////////////////////////////////////////////////////////////////////
	// by ID
	////////////////////////////////////////////////////////////////////////
	
	private PreparedStatement byIdStmt(Schema table, Connection db
			, Object id, StringBuilder before) throws SQLException
	{
		IdWhereMaker.getSql(before, table);
		String sql = before.toString();
		Object[] params = IdWhereMaker.getParam(id);
		if (log.isLoggable(Level.FINE))
		{
			log.fine(sql);
			log.fine(Arrays.toString(params));
		}
		
		PreparedStatement stmt = null;
		stmt = db.prepareStatement(sql);
		for (int i=0; i<params.length; ++i) {
			stmt.setObject(i+1, params[i]);
		}
		return stmt;
	}
	
	public boolean removeById(Schema table, Connection db, Object id)
	{
		StringBuilder sqlBuilder = new StringBuilder("delete ");
		PreparedStatement stmt = null;
		try {
			stmt = byIdStmt(table, db, id, sqlBuilder);
			return stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseStatement(stmt);
		}
	}
	
	public boolean loadById(Schema table, Connection db, Object id, Object obj)
	{
		StringBuilder sqlBuilder = new StringBuilder();
		SelectMaker.getSql(sqlBuilder, table);
		PreparedStatement stmt = null;
		ResultSet set = null;
		try {
			stmt = byIdStmt(table, db, id, sqlBuilder);
			//stmt.setMaxRows(1);
			set = stmt.executeQuery();
			if (set.next())
			{
				return SqlUtil.fillToObj(table, obj, set);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseResultSet(set);
			DbUtil.colseStatement(stmt);
		}

		return false;
	}
	
	public boolean existById(Schema table, Connection db, Object id)
	{
		StringBuilder sqlBuilder = new StringBuilder("select *");
		PreparedStatement stmt = null;
		ResultSet set = null;
		try {
			stmt = byIdStmt(table, db, id, sqlBuilder);
			//stmt.setMaxRows(1);
			set = stmt.executeQuery();
			if (set.next())
			{
				return true;
			}
			return false;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseResultSet(set);
			DbUtil.colseStatement(stmt);
		}
	}
	
	////////////////////////////////////////////////////////////////////////
	// table Op
	////////////////////////////////////////////////////////////////////////
	
	private void createIndex(Schema table, Connection db, Field field) {
		Statement stmt = null;
		String sql = TableMaker.createIndex(table.getName(), field.getName());
		if (log.isLoggable(Level.FINE))
		{
			log.fine(sql);
		}
		stmt = null;
		try {
			stmt = db.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseStatement(stmt);
		}
	}
	
	public void createTable(Schema table, Connection db)
	{
		String sql = TableMaker.createTable(table, dialect);
		if (log.isLoggable(Level.FINE))
		{
			log.fine(sql);
		}
		
		Statement stmt = null;
		try {
			stmt = db.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseStatement(stmt);
		}

		for (int i=0; i<table.size(); ++i)
		{
			Field f = table.get(i);
			if (f.isIndexed())
			{
				createIndex(table, db, f);
			}
		}
	}

	public void dropTable(Schema table, Connection db)
	{
		String sql = TableMaker.dropTable(table);
		if (log.isLoggable(Level.FINE))
		{
			log.fine(sql);
		}
		
		Statement stmt = null;
		try {
			stmt = db.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseStatement(stmt);
		}
	}
	
}
