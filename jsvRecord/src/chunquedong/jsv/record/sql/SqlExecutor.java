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

public class SqlExecutor {
	static Logger log = Logger.getLogger("jsvRecord");
	
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
	
	public void update(Schema table, Connection db, Object obj)
	{
		String sql = UpdateMaker.getSql(table);
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
	

	public List<Object> select(Schema table, Connection db, Object obj
			, String orderby, int offset, int limit)
	{
		String sql = SelectMaker.getSql(table) + WhereMaker.getSql(table, obj);
		if (!orderby.isEmpty()) sql += " " + orderby;
		Object[] params = WhereMaker.getParam(table, obj);
		if (log.isLoggable(Level.FINE))
		{
			log.fine(sql);
			log.fine(Arrays.toString(params));
		}
		
		PreparedStatement stmt = null;
		ResultSet set = null;
		List<Object> list = new ArrayList<Object>();
		try {
			stmt = db.prepareStatement(sql);
			for (int i=0; i<params.length; ++i) {
				stmt.setObject(i+1, params[i]);
			}
			stmt.setMaxRows(offset+limit);
			set = stmt.executeQuery();
			set.absolute(offset);
			while (set.next())
			{
				Object v = SqlUtil.getInstance(table, set);
				list.add(v);
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
		String sql = SelectMaker.getSql(table) + WhereMaker.getSql(table, obj);
		if (!orderby.isEmpty()) sql += " " + orderby;
		Object[] params = WhereMaker.getParam(table, obj);
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
			stmt.setMaxRows(1);
			set = stmt.executeQuery();
			if (set.next())
			{
				Object v = SqlUtil.getInstance(table, set);
				return v;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseResultSet(set);
			DbUtil.colseStatement(stmt);
		}
		return null;
	}
	
	public List<Object> selectWhere(Schema table, Connection db, String condition
			, int offset, int limit)
	{
		String sql = SelectMaker.getSql(table) + " from " + table.getName();
		if (!condition.isEmpty()) sql += " " + condition;
		if (log.isLoggable(Level.FINE))
		{
			log.fine(sql);
		}
		
		Statement stmt = null;
		ResultSet set = null;
		List<Object> list = new ArrayList<Object>();
		try {
			stmt = db.createStatement();
			stmt.setMaxRows(offset+limit);
			set = stmt.executeQuery(sql);
			set.absolute(offset);
			while (set.next())
			{
				Object v = SqlUtil.getInstance(table, set);
				list.add(v);
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
		String sql = "delete " + WhereMaker.getSql(table, obj);
		Object[] params = WhereMaker.getParam(table, obj);
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
		String sql = "select count(*)" + WhereMaker.getSql(table, obj);
		Object[] params = WhereMaker.getParam(table, obj);
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
			, Object id, String before)
	{
		String sql = before + IdWhereMaker.getSql(table);
		Object[] params = IdWhereMaker.getParam(table, id);
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
			return stmt;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseStatement(stmt);
		}
	}
	
	public boolean removeById(Schema table, Connection db, Object id)
	{
		PreparedStatement stmt = byIdStmt(table, db, id, "delete ");
		try {
			return stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseStatement(stmt);
		}
	}
	
	public Object findById(Schema table, Connection db, Object id)
	{
		PreparedStatement stmt = byIdStmt(table, db, id, SelectMaker.getSql(table));

		ResultSet set = null;
		try {
			stmt.setMaxRows(1);
			set = stmt.executeQuery();
			if (set.next())
			{
				Object v = SqlUtil.getInstance(table, set);
				return v;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtil.colseResultSet(set);
			DbUtil.colseStatement(stmt);
		}

		return null;
	}
	
	public boolean existById(Schema table, Connection db, Object id)
	{
		PreparedStatement stmt = byIdStmt(table, db, id, "select *");
		ResultSet set = null;
		try {
			stmt.setMaxRows(1);
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
		String sql = TableMaker.createTable(table);
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
