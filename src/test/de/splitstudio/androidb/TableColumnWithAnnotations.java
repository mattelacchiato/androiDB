package de.splitstudio.androidb;

import android.database.sqlite.SQLiteDatabase;

public class TableColumnWithAnnotations extends Table {

	public static final String SQL = "CREATE TABLE IF NOT EXISTS TableColumnWithAnnotations ( _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)";

	public TableColumnWithAnnotations(final SQLiteDatabase db) {
		super(db);
	}

}
