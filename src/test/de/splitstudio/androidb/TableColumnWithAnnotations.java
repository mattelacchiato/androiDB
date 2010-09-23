package de.splitstudio.androidb;

import java.lang.reflect.Field;

import android.database.sqlite.SQLiteDatabase;

public class TableColumnWithAnnotations extends Table {

	public static final String SQL = "CREATE TABLE IF NOT EXISTS TableColumnWithAnnotations ( _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)";

	public TableColumnWithAnnotations(final SQLiteDatabase db) {
		super(db);
	}

	@Override
	protected void setValue(final Field field, final Object value) throws IllegalArgumentException,
			IllegalAccessException {
		field.set(this, value);
	}

	@Override
	protected Object getValue(final Field field) throws IllegalArgumentException, IllegalAccessException {
		return field.get(this);
	}

}
