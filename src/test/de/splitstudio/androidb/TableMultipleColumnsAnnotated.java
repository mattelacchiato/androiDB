package de.splitstudio.androidb;

import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.Column;

public class TableMultipleColumnsAnnotated extends Table {
	public TableMultipleColumnsAnnotated(final SQLiteDatabase db) {
		super(db);
	}

	public static final String SQL = "CREATE TABLE IF NOT EXISTS "
			+ TableMultipleColumnsAnnotated.class.getSimpleName()
			+ " ( id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, text TEXT, amount REAL)";

	@Column
	protected String text;

	@Column
	protected float amount;

}
