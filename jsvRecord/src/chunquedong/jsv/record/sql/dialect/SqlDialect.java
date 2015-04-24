package chunquedong.jsv.record.sql.dialect;

import chunquedong.jsv.record.model.Field;
import chunquedong.jsv.record.model.Schema;

public class SqlDialect {

	public void createTableColumn(Schema table, Field f, StringBuilder sql) {
		sql.append(escaptSqlWord(f.getName()));
		sql.append(" ");

		if (table.isAutoGenerateId() && table.getPk() == f) {
			sql.append("integer PRIMARY KEY ");
			sql.append(autoIncrement());
		} else {
			sql.append(f.getSqlType());
		}
	}

	public String autoIncrement() {
		return "autoincrement";
	}
	
	public static String escaptSqlWord(String w) {
		return "`" + w +"`";
	}
}
