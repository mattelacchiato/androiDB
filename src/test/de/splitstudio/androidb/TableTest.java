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

public class TableTest {

	private SQLiteDatabase db;

	private Cursor cursor;

	private Object[] mocks;

	@Before
	public void setUp() {
		db = createMock(SQLiteDatabase.class);
		cursor = createMock(Cursor.class);
		mocks = new Object[] { db, cursor };
		Table.createdTables.clear();
	}

	@Test
	public void createTable_allAnnotions_executeCorrectSql() {
		db.execSQL(TableColumnWithAnnotations.SQL);
		EasyMock.expectLastCall();

		replay(mocks);
		new TableColumnWithAnnotations(db);
		verify(mocks);
	}

	@Test
	public void createTable_multipleColumns_executeCorrectSql() {
		db.execSQL(TableMultipleColumns.SQL);
		EasyMock.expectLastCall();

		replay(mocks);
		new TableMultipleColumns(db);
		verify(mocks);
	}

	@Test
	public void fill_emptyCursor_false() {
		EasyMock.expect(cursor.getCount()).andReturn(0);

		replay(mocks);
		Table.createdTables.add(TableMultipleColumns.class.getSimpleName());
		TableMultipleColumns table = new TableMultipleColumns(db);
		boolean result = table.fill(cursor);
		verify(mocks);

		assertFalse(result);
	}

	@Test
	public void fill_twoRowCursor_false() {
		EasyMock.expect(cursor.getCount()).andReturn(2);

		replay(mocks);
		Table.createdTables.add(TableMultipleColumns.class.getSimpleName());
		TableMultipleColumns table = new TableMultipleColumns(db);
		boolean result = table.fill(cursor);
		verify(mocks);

		assertFalse(result);
	}

	@Test
	public void fill_oneRowCursor_trueAndFilled() {
		Table.createdTables.add(TableColumnWithAnnotations.class.getSimpleName());
		TableColumnWithAnnotations table = new TableColumnWithAnnotations(db);
		EasyMock.expect(cursor.getCount()).andReturn(1);
		EasyMock.expect(cursor.getColumnIndex(Table.PRIMARY_KEY)).andReturn(0);
		EasyMock.expect(cursor.getLong(0)).andReturn(42L);

		replay(mocks);
		boolean result = table.fill(cursor);
		verify(mocks);

		assertTrue(result);
		assertThat(table.getId(), is(42L));
	}

	@Test
	public void insert_tableWithColumns_trueAndCorrectSqlExecuted() {
		String tableName = TableColumnWithAnnotations.class.getSimpleName();
		Table.createdTables.add(tableName);
		TableColumnWithAnnotations table = new TableColumnWithAnnotations(db);
		table.setId(42L);

		db.execSQL(String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, " _id", " " + "42"));
		EasyMock.expectLastCall();

		replay(mocks);
		assertThat(table.insert(), is(true));
		verify(mocks);
	}

	@Test
	public void update_tableWithPrimaryKey_trueAndCorrectSqlExecuted() {
		Table.createdTables.add(TableMultipleColumnsAnnotated.class.getSimpleName());
		TableMultipleColumnsAnnotated table = new TableMultipleColumnsAnnotated(db);
		table.setId(42L);
		table.amount = 3.14f;
		table.text = "foo";
		db.execSQL("UPDATE TableMultipleColumnsAnnotated SET text='foo', amount=3.14 WHERE id=42");
		EasyMock.expectLastCall();

		replay(mocks);
		boolean result = table.update();
		verify(mocks);

		assertThat(result, is(true));
	}

	@Test
	public void delete_tableWithPrimaryKey_trueAndCorrectSqlExecuted() {
		Table.createdTables.add(TableColumnWithAnnotations.class.getSimpleName());
		TableColumnWithAnnotations table = new TableColumnWithAnnotations(db);
		table.setId(42L);
		EasyMock.expect(db.delete(table.getClass().getSimpleName(), "WHERE id=42", null)).andReturn(1);
		replay(mocks);
		boolean result = table.delete();
		verify(mocks);

		assertThat(result, is(true));
	}

	@Test
	public void save_oldTable_update() {
		Table.createdTables.add(TableMultipleColumnsAnnotated.class.getSimpleName());
		TableMultipleColumnsAnnotated table = new TableMultipleColumnsAnnotated(db);
		table.setId(42L);
		db.execSQL("UPDATE TableMultipleColumnsAnnotated SET text=null, amount=0.0 WHERE id=42");
		EasyMock.expectLastCall();

		replay(mocks);
		boolean result = table.save();
		verify(mocks);
		assertThat(result, is(true));
	}

	@Test
	public void save_newTable_insert() {
		Table.createdTables.add(TableMultipleColumnsAnnotated.class.getSimpleName());
		TableMultipleColumnsAnnotated table = new TableMultipleColumnsAnnotated(db);
		db.execSQL(String.format("INSERT INTO %s (%s) VALUES (%s)", table.getClass().getSimpleName(),
			" _id, text, amount", " " + "null, null, 0.0"));
		EasyMock.expectLastCall();

		replay(mocks);
		boolean result = table.save();
		verify(mocks);
		assertThat(result, is(true));
	}

	@Test
	public void getColumns_returnsAllAnnotatedColumns() {
		Table table = new TableColumnWithAnnotations(db);
		assertThat(table.getColumns(), equalTo(new String[] { "_id" }));
	}

}