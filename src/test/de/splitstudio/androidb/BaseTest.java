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

	@Test
	public void createTable_allAnnotions_executeCorrectSql() {
		db.execSQL(TableColumnWithAnnotations.SQL);
		EasyMock.expectLastCall();

		replay(mocks);
		TableColumnWithAnnotations table = new TableColumnWithAnnotations(db);
		table.createTable();
		verify(mocks);
	}

	@Test
	public void createTable_multipleColumns_executeCorrectSql() {
		db.execSQL(TableMultipleColumns.SQL);
		EasyMock.expectLastCall();

		replay(mocks);
		TableMultipleColumns table = new TableMultipleColumns(db);
		table.createTable();
		verify(mocks);
	}

	@Test
	public void fill_emptyCursor_false() {
		EasyMock.expect(cursor.getCount()).andReturn(0);

		replay(mocks);
		TableMultipleColumns table = new TableMultipleColumns(db);
		boolean result = table.fill(cursor);
		verify(mocks);

		assertFalse(result);
	}

	@Test
	public void fill_twoRowCursor_false() {
		EasyMock.expect(cursor.getCount()).andReturn(2);

		replay(mocks);
		TableMultipleColumns table = new TableMultipleColumns(db);
		boolean result = table.fill(cursor);
		verify(mocks);

		assertFalse(result);
	}

	@Test
	public void fill_oneRowCursor_trueAndFilled() {
		TableColumnWithAnnotations table = new TableColumnWithAnnotations(db);
		EasyMock.expect(cursor.getCount()).andReturn(1);
		EasyMock.expect(cursor.getColumnIndex("id")).andReturn(0);
		EasyMock.expect(cursor.getInt(0)).andReturn(42);

		replay(mocks);
		boolean result = table.fill(cursor);
		verify(mocks);

		assertTrue(result);
		assertThat(table.getId(), is(42));
	}

	@Test
	public void insert_tableWithoutColumns_false() {
		replay(mocks);
		TableNoColumn table = new TableNoColumn(db);
		assertFalse(table.insert());
		verify(mocks);
	}

	@Test
	public void insert_tableWithColumns_trueAndCorrectSqlExecuted() {
		TableColumnWithAnnotations table = new TableColumnWithAnnotations(db);
		table.setId(42);

		db.execSQL(String.format("INSERT INTO %s (%s) VALUES (%s)", table.getClass().getSimpleName(), " id", " "
				+ "'42'"));
		EasyMock.expectLastCall();
		db.execSQL(TableColumnWithAnnotations.SQL);
		EasyMock.expectLastCall();

		replay(mocks);
		assertTrue(table.insert());
		verify(mocks);
	}

	@Test
	public void update_tableWithPrimaryKey_trueAndCorrectSqlExecuted() {
		TableMultipleColumnsAnnotated table = new TableMultipleColumnsAnnotated(db);
		table.id = 42;
		table.amount = 3.14f;
		table.text = "foo";
		db.execSQL("UPDATE TableMultipleColumnsAnnotated SET text='foo', amount='3.14' WHERE id='42'");
		EasyMock.expectLastCall();

		replay(mocks);
		boolean result = table.update();
		verify(mocks);

		assertThat(result, is(true));
	}

	@Test
	public void delete_tableWithPrimaryKey_trueAndCorrectSqlExecuted() {
		TableColumnWithAnnotations table = new TableColumnWithAnnotations(db);
		table.setId(42);
		EasyMock.expect(db.delete(table.getClass().getSimpleName(), "WHERE id='42'", null)).andReturn(1);
		replay(mocks);
		boolean result = table.delete();
		verify(mocks);

		assertThat(result, is(true));
	}
}
