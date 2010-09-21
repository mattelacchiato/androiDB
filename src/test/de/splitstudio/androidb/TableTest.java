package de.splitstudio.androidb;

import static org.easymock.EasyMock.createMock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Before;
import org.junit.Test;

import android.database.sqlite.SQLiteDatabase;

public class TableTest {

	private TableFixture table;

	private Base base;

	private SQLiteDatabase db;

	private final Object[] mocks = { db };

	@Before
	public void setUp() {
		this.table = new TableFixture();

		db = createMock(SQLiteDatabase.class);
		base = new Base(db);
	}

	@Test
	public void getColumns_returnsAllAnnotatedColumns() {
		assertThat(base.getColumns(TableFixture.class), equalTo(new String[] { "id" }));
	}

	@Test
	public void getColumns_noAnnotatedField_emptyArray() {
		assertThat(base.getColumns(TableFixtureNoFields.class), equalTo(new String[] {}));
	}

}
