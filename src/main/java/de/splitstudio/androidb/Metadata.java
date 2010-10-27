package de.splitstudio.androidb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.Column;

class Metadata extends Table {

	@Column(notNull = true)
	private int tableVersion;

	@Column(notNull = true)
	private String tableName;

	public Metadata(final SQLiteDatabase db) {
		super(db);
	}

	public boolean findByName(final String name) {
		Cursor c = db.query(getTableName(), getColumnNames(), "tableName='" + name + "'", null, null, null, null);
		return fillFirst(c);
	}

	public void setVersion(final int version) {
		this.tableVersion = version;
	}

	public int getTableVersion() {
		return tableVersion;
	}

	public Metadata setTable(final String table) {
		this.tableName = table;
		return this;
	}

	public String getTable() {
		return tableName;
	}

	/*..able to test*/
	Metadata setTableVersion(final int i) {
		this.tableVersion = i;
		return this;
	}

}
