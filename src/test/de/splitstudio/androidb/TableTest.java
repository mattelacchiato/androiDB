package de.splitstudio.androidb;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isNull;
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
import android.database.sqlite.SQLiteStatement;

public class TableTest {

	private SQLiteDatabase db;

	private SQLiteStatement statement;

	private Cursor cursor;

	private Object[] mocks;

	@Before
	public void setUp() {
		db = createMock(SQLiteDatabase.class);
		cursor = createMock(Cursor.class);
		statement = createMock(SQLiteStatement.class);
		mocks = new Object[] { db, cursor, statement };
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
		EasyMock.expect(cursor.moveToFirst()).andReturn(false);

		replay(mocks);
		Table.createdTables.add(TableMultipleColumns.class.getSimpleName());
		TableMultipleColumns table = new TableMultipleColumns(db);
		boolean result = table.fillFirst(cursor);
		verify(mocks);

		assertFalse(result);
	}

	@Test
	public void fill_oneRowCursor_trueAndFilled() {
		Table.createdTables.add(TableColumnWithAnnotations.class.getSimpleName());
		TableColumnWithAnnotations table = new TableColumnWithAnnotations(db);
		EasyMock.expect(cursor.getColumnIndex(Table.PRIMARY_KEY)).andReturn(0);
		EasyMock.expect(cursor.moveToFirst()).andReturn(true);
		EasyMock.expect(cursor.getLong(0)).andReturn(42L);

		replay(mocks);
		boolean result = table.fillFirst(cursor);
		verify(mocks);

		assertTrue(result);
		assertThat(table.getId(), is(42L));
	}

	@Test
	public void insert_tableWithColumns_trueAndCorrectSqlExecuted() {
		String tableName = TableColumnWithAnnotations.class.getSimpleName();
		String query = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, " _id", " " + "42");
		Table.createdTables.add(tableName);
		TableColumnWithAnnotations table = new TableColumnWithAnnotations(db);
		table.setId(42L);

		EasyMock.expect(db.compileStatement(query)).andReturn(statement);
		EasyMock.expect(statement.executeInsert()).andReturn(42L);

		replay(mocks);
		assertThat(table.insert(), is(true));
		verify(mocks);
	}

	@Test
	public void update_tableWithPrimaryKey_trueAndCorrectSqlExecuted() {
		Table.createdTables.add(TableMultipleColumnsAnnotated.class.getSimpleName());
		TableMultipleColumnsAnnotated table = new TableMultipleColumnsAnnotated(db);
		table.setId(42L);
		table.setAmount(3.14f);
		table.setText("foo");
		db.execSQL("UPDATE TableMultipleColumnsAnnotated SET text='foo', amount=3.14 WHERE _id=42");
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
		EasyMock.expect(db.delete(table.getClass().getSimpleName(), "_id=42", null)).andReturn(1);
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
		db.execSQL("UPDATE TableMultipleColumnsAnnotated SET text=null, amount=0.0 WHERE _id=42");
		expectLastCall();

		replay(mocks);
		boolean result = table.save();
		verify(mocks);
		assertThat(result, is(true));
	}

	@Test
	public void save_newTable_runInsertAndFillId() {
		Table.createdTables.add(TableMultipleColumnsAnnotated.class.getSimpleName());
		TableMultipleColumnsAnnotated table = new TableMultipleColumnsAnnotated(db);
		Long expectedId = 3L;
		String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", table.getClass().getSimpleName(),
			" _id, text, amount", " " + "null, null, 0.0");

		EasyMock.expect(db.compileStatement(sql)).andReturn(statement);
		EasyMock.expect(statement.executeInsert()).andReturn(expectedId);

		replay(mocks);
		boolean result = table.save();
		verify(mocks);
		assertThat(result, is(true));
		assertThat(table._id, is(expectedId));
	}

	@Test
	public void getColumns_returnsAllAnnotatedColumns() {
		Table table = new TableColumnWithAnnotations(db);
		assertThat(table.getColumns(), equalTo(new String[] { "_id" }));
	}

	@Test
	public void find_withoutParameterAndId_returnsFalse() {
		Table table = new TableColumnWithAnnotations(db);
		assertThat(table.find(), equalTo(false));
	}

	@Test
	public void find_withId_executesCorrectSQLAndReturnTrue() {
		Table.createdTables.add(TableColumnWithAnnotations.class.getSimpleName());
		Table table = new TableColumnWithAnnotations(db);
		String tableName = TableColumnWithAnnotations.class.getSimpleName();
		Long id = 42L;
		table._id = id;

		expect(
			db.query(eq(tableName), aryEq(new String[] { "_id" }), eq("_id=" + id), isNull(String[].class),
				isNull(String.class), isNull(String.class), isNull(String.class))).andReturn(cursor);
		expect(cursor.moveToFirst()).andReturn(true);
		expect(cursor.getColumnIndex("_id")).andReturn(0);
		expect(cursor.getLong(0)).andReturn(id);

		replay(mocks);
		assertThat(table.find(), equalTo(true));
		verify(mocks);
	}
}
