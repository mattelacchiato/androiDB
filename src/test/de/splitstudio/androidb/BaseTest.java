package de.splitstudio.androidb;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.Column;

public class BaseTest {

	private Base base;

	private SQLiteDatabase db;

	private Cursor cursor;

	private Object[] mocks;

	@Before
	public void setUp() {
		db = createMock(SQLiteDatabase.class);
		cursor = createMock(Cursor.class);
		base = new Base(db);
		mocks = new Object[] { db, cursor };
	}

	@Test
	public void getColumns_returnsAllAnnotatedColumns() {
		assertThat(base.getColumns(TableColumnWithAnnotations.class), equalTo(new String[] { "id" }));
	}

	@Test
	public void getColumns_noAnnotatedField_emptyArray() {
		assertThat(base.getColumns(TableNoColumn.class), equalTo(new String[] {}));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createTable_noColumn_throwsException() {
		replay(mocks);
		base.createTable(new TableNoColumn());
		verify(mocks);
	}

	@Test
	public void createTable_allAnnotions_executeCorrectSql() {
		db.execSQL(TableColumnWithAnnotations.SQL);
		EasyMock.expectLastCall();

		replay(mocks);
		base.createTable(new TableColumnWithAnnotations());
		verify(mocks);
	}

	@Test
	public void createTable_multipleColumns_executeCorrectSql() {
		db.execSQL(TableMultipleColumn.SQL);
		EasyMock.expectLastCall();

		replay(mocks);
		base.createTable(new TableMultipleColumn());
		verify(mocks);
	}

	@Test
	public void fill_emptyCursor_false() {
		EasyMock.expect(cursor.getCount()).andReturn(0);

		replay(mocks);
		boolean result = base.fill(cursor, new TableMultipleColumn());
		verify(mocks);

		assertFalse(result);
	}

	@Test
	public void fill_twoRowCursor_false() {
		EasyMock.expect(cursor.getCount()).andReturn(2);

		replay(mocks);
		boolean result = base.fill(cursor, new TableMultipleColumn());
		verify(mocks);

		assertFalse(result);
	}

	@Test
	public void fill_oneRowCursor_trueAndFilled() {
		TableColumnWithAnnotations table = new TableColumnWithAnnotations();
		EasyMock.expect(cursor.getCount()).andReturn(1);
		EasyMock.expect(cursor.getColumnIndex("id")).andReturn(0);
		EasyMock.expect(cursor.getInt(0)).andReturn(42);

		replay(mocks);
		boolean result = base.fill(cursor, table);
		verify(mocks);

		assertTrue(result);
		assertThat(table.id, is(42));
	}

	private class TableMultipleColumn implements Table {
		public static final String SQL = "CREATE TABLE IF NOT EXISTS TableMultipleColumn ( id INTEGER, text TEXT, amount REAL)";

		@Column
		Integer id;

		@Column
		String text;

		@Column
		float amount;

		@Override
		public boolean isNew() {
			return id == null;
		}
	}

	private class TableColumnWithAnnotations implements Table {
		public static final String SQL = "CREATE TABLE IF NOT EXISTS TableColumnWithAnnotations ( id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL )";

		@Column(primaryKey = true, autoIncrement = true, notNull = true)
		Integer id;

		@Override
		public boolean isNew() {
			return id == null;
		}
	}

	private class TableNoColumn implements Table {
		Integer foo;

		@Override
		public boolean isNew() {
			return true;
		}
	}
}
