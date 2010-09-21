package de.splitstudio.androidb;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import android.database.sqlite.SQLiteDatabase;
import de.splitstudio.androidb.annotation.Column;

public class TableTest {

	private Base base;

	private SQLiteDatabase db;

	private Object[] mocks;

	@Before
	public void setUp() {
		db = createMock(SQLiteDatabase.class);
		base = new Base(db);
		mocks = new Object[] { db };
	}

	@Test
	public void getColumns_returnsAllAnnotatedColumns() {
		assertThat(base.getColumns(TableSingleColumn.class), equalTo(new String[] { "foo" }));
	}

	@Test
	public void getColumns_noAnnotatedField_emptyArray() {
		assertThat(base.getColumns(TableNoColumn.class), equalTo(new String[] {}));
	}

	@Test
	public void createTable_executeCorrectSql() {
		db.execSQL("CREATE TABLE IF NOT EXISTS TableMultipleColumn ( id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ) ");
		EasyMock.expectLastCall();

		replay(mocks);
		base.createTable(new TableMultipleColumn());
		verify(mocks);
	}

	private class TableMultipleColumn implements Table {
		@Column(primaryKey = true, autoIncrement = true)
		Integer id;
	}

	private class TableSingleColumn implements Table {
		@Column
		Integer foo;
	}

	private class TableNoColumn implements Table {
		Integer foo;
	}
}
