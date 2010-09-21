package de.splitstudio.androidb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author mb
 */
public class Base {

	private final SQLiteDatabase db;

	public Base(final SQLiteDatabase db) {
		this.db = db;
	}

	public Base(final Context context, final String databaseName) {
		db = DatabaseFactory.create(context, databaseName);
	}

}
