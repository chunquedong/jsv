package chunquedong.jsv.record.sql.dialect;

public class SqliteDialect extends SqlDialect {

	@Override
	public String autoIncrement() {
		return "autoincrement";
	}

}
