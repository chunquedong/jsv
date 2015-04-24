package chunquedong.jsv.record.sql.dialect;

public class H2Dialect extends SqlDialect {

	@Override
	public String autoIncrement() {
		return "AUTO_INCREMENT";
	}

}
