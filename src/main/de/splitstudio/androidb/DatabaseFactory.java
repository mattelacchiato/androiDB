package de.splitstudio.androidb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseFactory extends SQLiteOpenHelper {

	private static final int INITIAL_VERSION = 1;

	public static SQLiteDatabase create(final Context context, final String name) {
		return new DatabaseFactory(context, name, null, INITIAL_VERSION).getWritableDatabase();
	}

	private DatabaseFactory(final Context context, final String name, final CursorFactory factory, final int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {}

}
