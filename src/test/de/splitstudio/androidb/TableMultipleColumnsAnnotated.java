package de.splitstudio.androidb;

import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.Column;

public class TableMultipleColumnsAnnotated extends Table {
	public TableMultipleColumnsAnnotated(final SQLiteDatabase db) {
		super(db);
	}

	public static final String SQL = "CREATE TABLE IF NOT EXISTS "
			+ TableMultipleColumnsAnnotated.class.getSimpleName()
			+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, text TEXT, amount REAL)";

	@Column
	private String text;

	@Column
	private float amount;

	public void setAmount(final float f) {
		amount = f;
	}

	public void setText(final String string) {
		text = string;
	}

}
