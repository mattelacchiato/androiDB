package de.splitstudio.androidb;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
		db.execSQL(TableMultipleColumns.SQL);
		EasyMock.expectLastCall();

		replay(mocks);
		base.createTable(new TableMultipleColumns());
		verify(mocks);
	}

	@Test
	public void fill_emptyCursor_false() {
		EasyMock.expect(cursor.getCount()).andReturn(0);

		replay(mocks);
		boolean result = base.fill(new TableMultipleColumns(), cursor);
		verify(mocks);

		assertFalse(result);
	}

	@Test
	public void fill_twoRowCursor_false() {
		EasyMock.expect(cursor.getCount()).andReturn(2);

		replay(mocks);
		boolean result = base.fill(new TableMultipleColumns(), cursor);
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
		boolean result = base.fill(table, cursor);
		verify(mocks);

		assertTrue(result);
		assertThat(table.id, is(42));
	}

}
