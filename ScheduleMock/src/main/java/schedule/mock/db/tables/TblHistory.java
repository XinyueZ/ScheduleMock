package schedule.mock.db.tables;

public interface TblHistory {

	String TABLE_NAME = "history";
	String COL_NAME = "name";
	String COL_LATLNG = "latlng";
	String COL_CREATE_TIME = "create_time";
	String SQL_CREATE = String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY,%s TEXT,%s INTEGER)", TABLE_NAME, COL_LATLNG, COL_NAME, COL_CREATE_TIME);
	String STMT_INSERT = String.format("INSERT INTO %s (%s,%s,%s) VALUES (?,?,?)", TABLE_NAME, COL_LATLNG, COL_NAME, COL_CREATE_TIME);
	String STMT_SELECT_ALL = String.format("SELECT * FROM %s ORDER BY create_time DESC", TABLE_NAME);
	String STMT_SELECT_LAST_COUNT = String.format("SELECT * FROM %s ORDER BY create_time DESC LIMIT  ", TABLE_NAME);
	String STMT_SELECT_BY_LATLNG = String.format("SELECT %s FROM %s WHERE %s=?", COL_NAME, TABLE_NAME, COL_LATLNG);
}
