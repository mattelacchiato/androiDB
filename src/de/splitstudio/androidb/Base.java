package de.splitstudio.androidb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class Base extends SQLiteOpenHelper {

	private final SQLiteDatabase db;

	public Base(final Context context, final String name, final CursorFactory factory, final int version) {
		super(context, name, factory, version);
		db = getWritableDatabase();
	}

	@Override
	public void onCreate(final SQLiteDatabase arg0) {

	}

	@Override
	public void onUpgrade(final SQLiteDatabase arg0, final int arg1, final int arg2) {
	}

	public Cursor getById(final Class<Table> tableClass, final long id){
		return db.query(tableClass.getSimpleName(), Table.getColumns(tableClass), "WHERE id = "+id, null,null,null,null);
	}

}
