package de.splitstudio.androidb;

import org.junit.Before;
import org.junit.Test;

import android.database.sqlite.SQLiteDatabase;

public class TableTestFunctional {

	SQLiteDatabase db;

	@Before
	public void setUp() {
		db = SQLiteDatabase.openOrCreateDatabase("testdb", null);
	}

	@Test
	public void theTruth() {
		Table table = new TableMultipleColumns(db);
		table._id = 21L;
		table.save();
	}
}
