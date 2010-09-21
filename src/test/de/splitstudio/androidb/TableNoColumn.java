package de.splitstudio.androidb;

import android.database.sqlite.SQLiteDatabase;

public class TableNoColumn extends Table {
	public TableNoColumn(final SQLiteDatabase db) {
		super(db);
	}

	Integer foo;

	@Override
	public boolean isNew() {
		return true;
	}
}
