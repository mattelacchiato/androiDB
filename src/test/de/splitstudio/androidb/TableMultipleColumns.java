package de.splitstudio.androidb;

import java.lang.reflect.Field;

import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.Column;

public class TableMultipleColumns extends Table {
	public TableMultipleColumns(final SQLiteDatabase db) {
		super(db);
	}

	public static final String SQL = "CREATE TABLE IF NOT EXISTS " + TableMultipleColumns.class.getSimpleName()
			+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, text TEXT, amount REAL)";

	@Column
	protected String text;

	@Column
	protected float amount;

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
