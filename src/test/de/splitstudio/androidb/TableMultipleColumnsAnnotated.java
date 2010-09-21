package de.splitstudio.androidb;

import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.Column;

public class TableMultipleColumnsAnnotated extends Table {
	public TableMultipleColumnsAnnotated(final SQLiteDatabase db) {
		super(db);
	}

	public static final String SQL = "CREATE TABLE IF NOT EXISTS "
			+ TableMultipleColumnsAnnotated.class.getSimpleName()
			+ " ( id INTEGER PRIMARY KEY, text TEXT, amount REAL)";

	@Column(primaryKey = true)
	Integer id;

	@Column
	String text;

	@Column
	float amount;

	@Override
	public boolean isNew() {
		return id == null;
	}
}
